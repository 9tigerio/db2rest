package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SqlTemplateStoreTest {

    @TempDir
    Path base;

    private Path root;
    private Path outside;

    @BeforeEach
    void setUp() throws IOException {
        root = Files.createDirectories(base.resolve("templates"));
        outside = Files.createDirectories(base.resolve("outside"));
        Files.writeString(root.resolve("select_all.sql"), "SELECT * FROM film");
        Files.writeString(outside.resolve("secret.sql"), "SELECT * FROM secrets");
        Files.writeString(base.resolve("secret.sql"), "SELECT * FROM secrets");
    }

    private SqlTemplateStore store(SqlTemplateLimits limits) {
        return new SqlTemplateStore(root::toString, limits);
    }

    private SqlTemplateStore store() {
        return store(SqlTemplateLimits.DEFAULTS);
    }

    @Test
    void loadsTemplateFromRoot() {
        assertThat(store().load("select_all").source()).isEqualTo("SELECT * FROM film");
    }

    @Test
    void loadsTemplateWhenRootIsGivenAsRelativeOrUnnormalizedPath() {
        SqlTemplateStore store = new SqlTemplateStore(
                () -> root.resolve("..").resolve("templates").resolve(".").toString(), SqlTemplateLimits.DEFAULTS);
        assertThat(store.load("select_all").source()).isEqualTo("SELECT * FROM film");
    }

    @ParameterizedTest
    @ValueSource(strings = {"../secret", "../outside/secret", "..", "/etc/passwd", "..\\secret",
            "%2e%2e%2fsecret", "C:\\secret", "\\\\host\\share\\secret", "outside/secret", "missing", "con"})
    void rejectsNamesOutsideTheGrammarOrMissingFiles(String name) {
        assertThatThrownBy(() -> store().load(name)).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void rejectsAbsolutePathToRealFile() {
        String absolute = outside.resolve("secret").toString();
        assertThatThrownBy(() -> store().load(absolute)).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void rejectsSymlinkEscapingRoot() throws IOException {
        assumeTrue(createSymlink(root.resolve("escape.sql"), outside.resolve("secret.sql")));
        assertThatThrownBy(() -> store().load("escape")).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void rejectsSymlinkEvenWhenTargetIsInsideRoot() throws IOException {
        assumeTrue(createSymlink(root.resolve("alias.sql"), root.resolve("select_all.sql")));
        assertThatThrownBy(() -> store().load("alias")).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void acceptsRootThatIsItselfASymlink() throws IOException {
        Path link = base.resolve("linked-root");
        assumeTrue(createSymlink(link, root));
        SqlTemplateStore store = new SqlTemplateStore(link::toString, SqlTemplateLimits.DEFAULTS);
        assertThat(store.load("select_all").source()).isEqualTo("SELECT * FROM film");
    }

    @Test
    void rejectsDirectoryNamedLikeTemplate() throws IOException {
        Files.createDirectories(root.resolve("dir.sql"));
        assertThatThrownBy(() -> store().load("dir")).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void missingOrUnsetRootFailsClosed() {
        assertThatThrownBy(() -> new SqlTemplateStore(() -> null, SqlTemplateLimits.DEFAULTS).load("select_all"))
                .isInstanceOf(SqlTemplateNotFoundException.class);
        assertThatThrownBy(() -> new SqlTemplateStore(() -> base.resolve("nope").toString(),
                SqlTemplateLimits.DEFAULTS).load("select_all"))
                .isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void rejectsTemplatesOverSourceLimit() throws IOException {
        Files.writeString(root.resolve("big.sql"), "SELECT " + "1,".repeat(100) + "1");
        Files.writeString(root.resolve("huge.sql"), "x".repeat(10_000));
        SqlTemplateStore store = store(new SqlTemplateLimits(100, 1000, 10, 4, 10));

        assertThatThrownBy(() -> store.load("big"))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("longer than 100 characters");
        assertThatThrownBy(() -> store.load("huge"))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("too large");
    }

    @Test
    void cachesUntilFileChanges() throws IOException {
        SqlTemplateStore store = store();
        Path file = root.resolve("select_all.sql");

        store.load("select_all");
        store.load("select_all");
        assertThat(store.stats()).extracting(SqlTemplateStore.CacheStats::misses, SqlTemplateStore.CacheStats::hits,
                SqlTemplateStore.CacheStats::reloads).containsExactly(1L, 1L, 0L);

        Files.writeString(file, "SELECT film_id FROM film");
        Files.setLastModifiedTime(file, FileTime.from(Instant.now().plusSeconds(5)));

        assertThat(store.load("select_all").source()).isEqualTo("SELECT film_id FROM film");
        assertThat(store.stats().reloads()).isEqualTo(1);
        assertThat(store.load("select_all").source()).isEqualTo("SELECT film_id FROM film");
        assertThat(store.stats().hits()).isEqualTo(2);
    }

    @Test
    void reloadsWhenSizeChangesEvenIfTimestampDoesNot() throws IOException {
        SqlTemplateStore store = store();
        Path file = root.resolve("select_all.sql");
        FileTime fixed = Files.getLastModifiedTime(file);
        store.load("select_all");

        Files.writeString(file, "SELECT 1");
        Files.setLastModifiedTime(file, fixed);

        assertThat(store.load("select_all").source()).isEqualTo("SELECT 1");
    }

    @Test
    void deletedTemplateIsNotServedFromCache() throws IOException {
        SqlTemplateStore store = store();
        store.load("select_all");
        Files.delete(root.resolve("select_all.sql"));
        assertThatThrownBy(() -> store.load("select_all")).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void cacheIsBoundedAndEvictsLeastRecentlyUsed() throws IOException {
        for (int i = 0; i < 10; i++) {
            Files.writeString(root.resolve("t" + i + ".sql"), "SELECT " + i);
        }
        SqlTemplateStore store = store(new SqlTemplateLimits(1000, 1000, 10, 4, 3));

        store.load("t0");
        store.load("t1");
        store.load("t2");
        store.load("t0"); // t1 is now least recently used
        store.load("t3"); // evicts t1

        assertThat(store.stats().size()).isEqualTo(3);
        assertThat(store.stats().evictions()).isEqualTo(1);

        long misses = store.stats().misses();
        store.load("t0");
        assertThat(store.stats().misses()).isEqualTo(misses);
        store.load("t1");
        assertThat(store.stats().misses()).isEqualTo(misses + 1);

        for (int i = 0; i < 10; i++) {
            store.load("t" + i);
        }
        assertThat(store.stats().size()).isEqualTo(3);
    }

    @Test
    void invalidateAllForcesReread() {
        SqlTemplateStore store = store();
        store.load("select_all");
        store.invalidateAll();
        assertThat(store.stats().size()).isZero();
        store.load("select_all");
        assertThat(store.stats().misses()).isEqualTo(2);
    }

    @Test
    void cacheEntryDoesNotBypassValidation() {
        SqlTemplateStore store = store();
        store.load("select_all");
        assertThatThrownBy(() -> store.load("./select_all")).isInstanceOf(SqlTemplateNotFoundException.class);
        assertThatThrownBy(() -> store.load("select_all/")).isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    @Timeout(60)
    void concurrentLoadsSeeConsistentContentWhileTemplatesChangeAndEvict() throws Exception {
        int templates = 8;
        for (int i = 0; i < templates; i++) {
            Files.writeString(root.resolve("c" + i + ".sql"), "SELECT " + i + " -- v0");
        }
        SqlTemplateStore store = store(new SqlTemplateLimits(1000, 1000, 10, 4, 4));
        int threads = 8;
        CyclicBarrier start = new CyclicBarrier(threads + 1);
        AtomicBoolean stop = new AtomicBoolean();
        ExecutorService pool = Executors.newFixedThreadPool(threads + 1);
        try {
            List<Future<Integer>> readers = new ArrayList<>();
            for (int t = 0; t < threads; t++) {
                int seed = t;
                readers.add(pool.submit(() -> {
                    start.await(10, TimeUnit.SECONDS);
                    int loads = 0;
                    for (int n = 0; n < 2_000; n++) {
                        int i = (seed + n) % templates;
                        String source = store.load("c" + i).source();
                        assertThat(source).startsWith("SELECT " + i + " -- v");
                        loads++;
                    }
                    return loads;
                }));
            }
            Future<?> writer = pool.submit(() -> {
                start.await(10, TimeUnit.SECONDS);
                int version = 1;
                while (!stop.get()) {
                    Path file = root.resolve("c" + (version % templates) + ".sql");
                    Files.writeString(file.resolveSibling("tmp"), "SELECT " + (version % templates) + " -- v" + version);
                    Files.move(file.resolveSibling("tmp"), file, java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                            java.nio.file.StandardCopyOption.ATOMIC_MOVE);
                    version++;
                }
                return null;
            });

            long total = 0;
            for (Future<Integer> reader : readers) {
                total += reader.get(50, TimeUnit.SECONDS);
            }
            stop.set(true);
            writer.get(10, TimeUnit.SECONDS);

            SqlTemplateStore.CacheStats stats = store.stats();
            assertThat(total).isEqualTo(threads * 2_000L);
            assertThat(stats.hits() + stats.misses() + stats.reloads()).isEqualTo(total);
            assertThat(stats.size()).isLessThanOrEqualTo(4);
            assertThat(stats.evictions()).isPositive();
        } finally {
            stop.set(true);
            pool.shutdownNow();
        }
    }

    private static boolean createSymlink(Path link, Path target) {
        try {
            Files.createSymbolicLink(link, target);
            return true;
        } catch (IOException | UnsupportedOperationException e) {
            return false;
        }
    }
}
