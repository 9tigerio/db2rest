package com.homihq.db2rest.jdbc.dto;

import com.db2rest.jdbc.dialect.model.DbTable;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record CreateContext(
        String dbId,
        DbTable table,
        List<String> insertableColumns,
        List<InsertableColumn> insertableColumnList
) {

    private List<String> getColumnNames() {
        return insertableColumnList.stream()
                .map(c -> c.columnName)
                .toList();
    }

    private List<String> getParamNames() {
        return insertableColumnList.stream()
                .map(col -> {
                    String seq = col.sequence;

                    // Case 1: fn[...] raw SQL fragment
                    if (seq != null) {
                        String inner = FnUtil.extractFn(seq);
                        if (inner != null) {
                            // swap placeholder -> named bind for THIS column
                            String frag = FnUtil.substituteColumnPlaceholder(inner, col.columnName).trim();

                            // Optional: safety guard (tune or disable as you wish)
                            if (!FnUtil.isSafe(frag)) {
                                throw new IllegalArgumentException(
                                        "Unsafe fn[] fragment for column '" + col.columnName + "': " + frag);
                            }
                            return frag; // inline raw SQL; DB handles functions
                        }
                    }

                    // Case 2: explicit non-empty raw expression/sequence
                    if (!StringUtils.isBlank(seq)) {
                        return seq.trim();
                    }

                    // Case 3: default named bind
                    return ":" + col.columnName;
                })
                .toList();
    }

    public String renderColumns() {
        return StringUtils.join(getColumnNames(), ",");
    }

    public String renderParams() {
        return StringUtils.join(getParamNames(), ",");
    }

    /**
     * Internal utilities for fn[...] handling.
     */
    static final class FnUtil {

        /**
         * Extract the content of the first top-level fn[ ... ] block.
         * Returns null if the input is not in fn[...] form.
         * Robust to nested brackets and quoted strings.
         */
        static String extractFn(String text) {
            if (text == null) return null;
            int i = text.indexOf("fn[");
            if (i < 0) return null;

            int start = i + 3; // after "fn["
            int depth = 1;
            boolean inSingle = false, inDouble = false;

            for (int pos = start; pos < text.length(); pos++) {
                char c = text.charAt(pos);

                // toggle quote state (simple/naive escape handling)
                if (c == '\'' && !inDouble && (pos == 0 || text.charAt(pos - 1) != '\\')) {
                    inSingle = !inSingle;
                    continue;
                }
                if (c == '"' && !inSingle && (pos == 0 || text.charAt(pos - 1) != '\\')) {
                    inDouble = !inDouble;
                    continue;
                }

                if (inSingle || inDouble) continue;

                if (c == '[') depth++;
                else if (c == ']') {
                    depth--;
                    if (depth == 0) {
                        return text.substring(start, pos);
                    }
                }
            }
            // no matching closing bracket found
            return null;
        }

        /**
         * Replace common placeholders for the current column with its named bind.
         */
        static String substituteColumnPlaceholder(String fragment, String columnName) {
            if (fragment == null) return null;
            String bind = ":" + columnName;
            return fragment
                    .replace("<column_name>", bind)
                    .replace("<COLUMN_NAME>", bind)
                    .replace("${column_name}", bind);
        }

        /**
         * Ultra-light safety filter. Expand/relax as needed.
         * Blocks obvious multi-statement or DDL/DCL attempts.
         */
        static boolean isSafe(String fragment) {
            if (fragment == null) return true;
            String s = fragment.toUpperCase();
            return !( s.contains("--")
                    || s.contains("/*") || s.contains("*/")
                    || s.contains(";")
                    || s.contains(" EXEC ")
                    || s.contains(" CALL ")
                    || s.contains(" DROP ")
                    || s.contains(" ALTER ")
                    || s.contains(" CREATE ")
                    || s.contains(" GRANT ")
                    || s.contains(" REVOKE ") );
        }

        private FnUtil() {}
    }
}
