package com.homihq.db2rest.jdbc.sql;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Objects;

/**
 * Limits which tables and views have their metadata loaded, based on the {@code INCLUDED_TABLES}
 * setting (the {@code tables} list of a database entry).
 * <p>
 * Each pattern is an Ant-style pattern ({@code *} matches any characters, {@code ?} matches one)
 * and is matched case-insensitively. A pattern without a dot is matched against the table name
 * alone, so {@code film*} matches {@code film} and {@code film_actor} in every included schema.
 * A pattern with a dot is matched against {@code schema.table} (or {@code catalog.table} for
 * databases such as MySQL that have no schemas), for example {@code sales.order_*}.
 * <p>
 * With no patterns, every table is included.
 */
public final class TableFilter {

    public static final TableFilter ALL = new TableFilter(List.of());

    private final List<String> patterns;
    private final AntPathMatcher matcher = new AntPathMatcher(".");

    private TableFilter(List<String> patterns) {
        this.patterns = patterns;
        this.matcher.setCaseSensitive(false);
    }

    public static TableFilter of(List<String> patterns) {
        if (Objects.isNull(patterns)) {
            return ALL;
        }
        List<String> cleaned = patterns.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .toList();
        return cleaned.isEmpty() ? ALL : new TableFilter(cleaned);
    }

    public boolean includesAll() {
        return patterns.isEmpty();
    }

    /**
     * @param schemaOrCatalog the table's schema, or its catalog when the database has no schemas;
     *                        may be null
     * @param tableName       the table or view name
     */
    public boolean includes(String schemaOrCatalog, String tableName) {
        if (patterns.isEmpty()) {
            return true;
        }
        String qualified = StringUtils.isBlank(schemaOrCatalog) ? tableName : schemaOrCatalog + "." + tableName;
        for (String pattern : patterns) {
            String candidate = pattern.contains(".") ? qualified : tableName;
            if (matcher.match(pattern, candidate)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return includesAll() ? "all tables" : patterns.toString();
    }
}
