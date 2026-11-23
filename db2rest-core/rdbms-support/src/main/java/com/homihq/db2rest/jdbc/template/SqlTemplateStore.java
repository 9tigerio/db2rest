package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import com.homihq.db2rest.core.exception.SqlTemplateReadException;
import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Loads SQL template files from one configured directory and keeps a bounded cache of
 * their contents.
 * <p>
 * Every lookup, cached or not, goes through the same checks before any file is read:
 * <ol>
 *   <li>the name must satisfy {@link SqlTemplateName};</li>
 *   <li>the configured directory is resolved to its real path (following symlinks in
 *       the directory itself is allowed);</li>
 *   <li>the candidate {@code <dir>/<name>.sql} must sit directly in that directory, be
 *       a regular file and not be a symbolic link.</li>
 * </ol>
 * A cache entry is reused only while the file's size and modification time are
 * unchanged, so editing a template takes effect on the next request. The cache is an
 * LRU map limited to {@link SqlTemplateLimits#maxCachedTemplates()} entries. Its lock
 * only guards map access; file reads and template rendering happen outside it.
 */
@Slf4j
public class SqlTemplateStore {

    private static final String EXTENSION = ".sql";

    /** A loaded template. */
    public record Template(String name, String source, Optional<SqlTemplateOperation> declaredOperation) {
    }

    /** Cache counters for monitoring. */
    public record CacheStats(int size, int maxSize, long hits, long misses, long reloads, long evictions) {
    }

    private record Entry(Template template, FileTime modified, long size) {
    }

    private final Supplier<String> location;
    private final SqlTemplateLimits limits;
    private final Map<Path, Entry> cache;
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong reloads = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    /**
     * @param location supplies the configured template directory; read on each lookup so a
     *                 changed setting is picked up without stale entries being served
     */
    public SqlTemplateStore(Supplier<String> location, SqlTemplateLimits limits) {
        this.location = location;
        this.limits = limits;
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Path, Entry> eldest) {
                if (size() > limits.maxCachedTemplates()) {
                    evictions.incrementAndGet();
                    return true;
                }
                return false;
            }
        };
    }

    public Template load(String name) {
        if (!SqlTemplateName.isValid(name)) {
            throw new SqlTemplateNotFoundException(name);
        }
        Path file = resolve(name);

        BasicFileAttributes attributes;
        try {
            attributes = Files.readAttributes(file, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException e) {
            throw new SqlTemplateNotFoundException(name);
        } catch (IOException e) {
            log.error("Cannot stat template {}: {}", file, e.getMessage());
            throw new SqlTemplateReadException(name);
        }
        if (!attributes.isRegularFile()) {
            // directories, devices and symbolic links (even ones pointing inside the directory)
            throw new SqlTemplateNotFoundException(name);
        }

        Entry cached;
        synchronized (cache) {
            cached = cache.get(file);
        }
        if (cached != null && cached.size() == attributes.size()
                && cached.modified().equals(attributes.lastModifiedTime())) {
            hits.incrementAndGet();
            return cached.template();
        }

        Template template = read(name, file, attributes);
        synchronized (cache) {
            cache.put(file, new Entry(template, attributes.lastModifiedTime(), attributes.size()));
        }
        if (cached == null) {
            misses.incrementAndGet();
        } else {
            reloads.incrementAndGet();
            log.info("Reloaded changed SQL template {}", name);
        }
        return template;
    }

    /** Drops every cached template; the next request for each one reads the file again. */
    public void invalidateAll() {
        synchronized (cache) {
            cache.clear();
        }
    }

    public CacheStats stats() {
        int size;
        synchronized (cache) {
            size = cache.size();
        }
        return new CacheStats(size, limits.maxCachedTemplates(), hits.get(), misses.get(), reloads.get(),
                evictions.get());
    }

    private Path resolve(String name) {
        String configured = location.get();
        if (configured == null || configured.isBlank()) {
            log.error("SQL template directory is not configured");
            throw new SqlTemplateNotFoundException(name);
        }
        Path root;
        try {
            root = Path.of(configured).toAbsolutePath().normalize().toRealPath();
        } catch (IOException | InvalidPathException e) {
            log.error("SQL template directory {} is not usable: {}", configured, e.getMessage());
            throw new SqlTemplateNotFoundException(name);
        }

        Path file = root.resolve(name + EXTENSION).normalize();
        if (!root.equals(file.getParent())) {
            // cannot happen for a valid name; kept so containment does not rely on the grammar alone
            throw new SqlTemplateNotFoundException(name);
        }
        return file;
    }

    private Template read(String name, Path file, BasicFileAttributes attributes) {
        // a UTF-8 file of more than 4 bytes per allowed character is certainly too long
        long maxBytes = (long) limits.maxSourceChars() * 4;
        if (attributes.size() > maxBytes) {
            throw new SqlTemplateValidationException(name, "template file is too large");
        }
        String source;
        // NOFOLLOW_LINKS again on open, so a file swapped for a symlink after the check is not followed
        try (InputStream in = Files.newInputStream(file, StandardOpenOption.READ, LinkOption.NOFOLLOW_LINKS)) {
            byte[] bytes = in.readNBytes((int) Math.min(Integer.MAX_VALUE - 8, maxBytes + 1));
            if (bytes.length > maxBytes) {
                throw new SqlTemplateValidationException(name, "template file is too large");
            }
            source = StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
        } catch (IOException e) {
            log.error("Error reading template file {}: {}", file, e.getMessage());
            throw new SqlTemplateReadException(name);
        }
        if (source.length() > limits.maxSourceChars()) {
            throw new SqlTemplateValidationException(name,
                    "template is longer than " + limits.maxSourceChars() + " characters");
        }
        return new Template(name, source, SqlTemplateOperation.declaredIn(name, source));
    }
}
