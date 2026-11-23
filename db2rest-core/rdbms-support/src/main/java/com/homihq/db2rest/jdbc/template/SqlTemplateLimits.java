package com.homihq.db2rest.jdbc.template;

/**
 * Upper bounds applied to every SQL template request.
 *
 * @param maxSourceChars     largest template file, in characters, that will be loaded
 * @param maxRenderedChars   largest SQL, in characters, that rendering may produce
 * @param maxPlaceholders    most {@code {{ ... }}} placeholders allowed in the rendered SQL
 * @param maxParamDepth      deepest nesting of request values (for example a JSON body)
 *                           that is flattened into SQL parameters
 * @param maxCachedTemplates most templates kept in memory; least recently used are evicted
 */
public record SqlTemplateLimits(
        int maxSourceChars,
        int maxRenderedChars,
        int maxPlaceholders,
        int maxParamDepth,
        int maxCachedTemplates) {

    public static final SqlTemplateLimits DEFAULTS = new SqlTemplateLimits(
            64 * 1024,
            256 * 1024,
            500,
            8,
            256);

    public SqlTemplateLimits {
        if (maxSourceChars < 1 || maxRenderedChars < 1 || maxPlaceholders < 1
                || maxParamDepth < 1 || maxCachedTemplates < 1) {
            throw new IllegalArgumentException("SQL template limits must be positive");
        }
    }
}
