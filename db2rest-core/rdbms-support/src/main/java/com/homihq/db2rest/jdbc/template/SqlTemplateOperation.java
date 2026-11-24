package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateValidationException;

import java.util.Locale;
import java.util.Optional;

/**
 * The kind of statement a SQL template runs. It decides how the statement is executed:
 * {@link #QUERY} returns rows; {@link #INSERT}, {@link #UPDATE} and {@link #DELETE}
 * run inside the database's transaction template.
 * <p>
 * A template can declare its operation in a leading SQL comment:
 * <pre>
 * -- db2rest:operation=update
 * WITH stale AS (...) UPDATE film SET ...
 * </pre>
 * Only blank lines and {@code --} comment lines before the first statement line are
 * searched. Without a declaration the operation is taken from the first keyword of the
 * rendered SQL, after skipping whitespace, comments and opening parentheses:
 * {@code SELECT}, {@code WITH} and {@code VALUES} are queries, and {@code INSERT},
 * {@code UPDATE} and {@code DELETE} are what they say. Any other keyword is rejected,
 * so a template that starts with, for example, {@code MERGE} or {@code CALL} must
 * declare its operation.
 */
public enum SqlTemplateOperation {
    QUERY, INSERT, UPDATE, DELETE;

    static final String DIRECTIVE = "db2rest:operation";

    public boolean isWrite() {
        return this != QUERY;
    }

    /**
     * Reads the {@code -- db2rest:operation=...} declaration from the template source.
     */
    public static Optional<SqlTemplateOperation> declaredIn(String templateName, String source) {
        int pos = 0;
        int length = source.length();
        while (pos < length) {
            int eol = source.indexOf('\n', pos);
            int lineEnd = eol < 0 ? length : eol;
            String line = source.substring(pos, lineEnd).strip();
            pos = lineEnd + 1;

            if (line.isEmpty()) {
                continue;
            }
            if (!line.startsWith("--")) {
                break;
            }
            String comment = line.substring(2).strip();
            if (comment.startsWith(DIRECTIVE)) {
                String rest = comment.substring(DIRECTIVE.length()).strip();
                if (!rest.startsWith("=")) {
                    throw new SqlTemplateValidationException(templateName, "malformed operation declaration");
                }
                return Optional.of(parse(templateName, rest.substring(1).strip()));
            }
        }
        return Optional.empty();
    }

    /**
     * Works out the operation from the first keyword of the rendered SQL.
     *
     * @return the operation, or empty if the first keyword is not one this enum knows
     */
    public static Optional<SqlTemplateOperation> detect(String sql) {
        String keyword = firstKeyword(sql);
        return switch (keyword) {
            case "select", "with", "values" -> Optional.of(QUERY);
            case "insert" -> Optional.of(INSERT);
            case "update" -> Optional.of(UPDATE);
            case "delete" -> Optional.of(DELETE);
            default -> Optional.empty();
        };
    }

    /**
     * Picks the operation for a rendered template: the declaration when there is one,
     * otherwise the detected keyword. A declaration that contradicts an unambiguous
     * INSERT, UPDATE or DELETE keyword is rejected.
     */
    public static SqlTemplateOperation resolve(String templateName, Optional<SqlTemplateOperation> declared,
                                               String renderedSql) {
        Optional<SqlTemplateOperation> detected = detect(renderedSql);
        if (declared.isPresent()) {
            if (detected.isPresent() && detected.get().isWrite() && detected.get() != declared.get()) {
                throw new SqlTemplateValidationException(templateName,
                        "declared operation " + declared.get() + " does not match the " + detected.get() + " statement");
            }
            return declared.get();
        }
        return detected.orElseThrow(() -> new SqlTemplateValidationException(templateName,
                "cannot tell the statement type; add '-- " + DIRECTIVE + "=query|insert|update|delete'"));
    }

    private static SqlTemplateOperation parse(String templateName, String value) {
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "query", "select" -> QUERY;
            case "insert" -> INSERT;
            case "update" -> UPDATE;
            case "delete" -> DELETE;
            default -> throw new SqlTemplateValidationException(templateName, "unknown operation '"
                    + (value.length() > 20 ? value.substring(0, 20) + "..." : value) + "'");
        };
    }

    static String firstKeyword(String sql) {
        int i = 0;
        int n = sql.length();
        while (i < n) {
            char c = sql.charAt(i);
            if (Character.isWhitespace(c) || c == '(') {
                i++;
            } else if (c == '-' && i + 1 < n && sql.charAt(i + 1) == '-') {
                int eol = sql.indexOf('\n', i + 2);
                i = eol < 0 ? n : eol + 1;
            } else if (c == '/' && i + 1 < n && sql.charAt(i + 1) == '*') {
                int close = sql.indexOf("*/", i + 2);
                i = close < 0 ? n : close + 2;
            } else {
                break;
            }
        }
        int start = i;
        while (i < n && Character.isLetter(sql.charAt(i)) && i - start < 16) {
            i++;
        }
        return sql.substring(start, i).toLowerCase(Locale.ROOT);
    }
}
