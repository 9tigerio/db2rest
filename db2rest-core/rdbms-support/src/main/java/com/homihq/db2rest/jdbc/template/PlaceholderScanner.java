package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import com.homihq.db2rest.jdbc.dto.Placeholder;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds {@code {{ name | filter | ... }}} placeholders in rendered SQL and turns them
 * into named parameters ({@code :name}).
 * <p>
 * This replaces a regular expression with nested quantifiers that could backtrack
 * exponentially. The scanner makes a single forward pass: every character is examined
 * a bounded number of times, so the cost is linear in the length of the SQL whatever
 * the input looks like.
 * <p>
 * Rules:
 * <ul>
 *   <li>A placeholder runs from {@code {{} to the next {@code }}}. If the text between
 *       them contains {@code }}, it is not a placeholder and is copied unchanged (this
 *       keeps literals such as {@code '{{1,2},{3,4}}'} intact).</li>
 *   <li>The name is the text before the first {@code |}, trimmed, and may only contain
 *       ASCII letters, digits, {@code _} and {@code .}.</li>
 *   <li>Each {@code |}-separated part after the name is a filter (constraint) name made
 *       of ASCII letters, digits and {@code _}. All filters are returned, in order.</li>
 * </ul>
 * Anything else inside a placeholder is rejected rather than passed to the database.
 */
public final class PlaceholderScanner {

    public record Result(String sql, List<Placeholder> placeholders) {
    }

    private PlaceholderScanner() {
    }

    public static Result scan(String templateName, String sql, int maxPlaceholders) {
        StringBuilder out = new StringBuilder(sql.length());
        List<Placeholder> placeholders = new ArrayList<>();
        int copied = 0;
        int from = 0;

        while (true) {
            int start = sql.indexOf("{{", from);
            if (start < 0) {
                break;
            }
            int end = sql.indexOf("}}", start + 2);
            if (end < 0) {
                break; // no closing marker anywhere after this point, so no more placeholders
            }

            int lastBrace = -1;
            for (int i = start + 2; i < end; i++) {
                if (sql.charAt(i) == '}') {
                    lastBrace = i;
                }
            }
            if (lastBrace >= 0) {
                // not a placeholder; the next one can only start after this brace
                from = lastBrace + 1;
                continue;
            }

            if (placeholders.size() == maxPlaceholders) {
                throw new SqlTemplateValidationException(templateName,
                        "more than " + maxPlaceholders + " placeholders");
            }
            Placeholder placeholder = parse(templateName, sql.substring(start + 2, end));
            placeholders.add(placeholder);

            out.append(sql, copied, start).append(':').append(placeholder.namedParam());
            copied = end + 2;
            from = end + 2;
        }

        out.append(sql, copied, sql.length());
        return new Result(out.toString(), List.copyOf(placeholders));
    }

    private static Placeholder parse(String templateName, String body) {
        String[] parts = body.split("\\|", -1);
        String name = parts[0].trim();
        if (!isName(name, true)) {
            throw new SqlTemplateValidationException(templateName, "invalid placeholder name '" + abbreviate(name) + "'");
        }
        List<String> filters = new ArrayList<>(parts.length - 1);
        for (int i = 1; i < parts.length; i++) {
            String filter = parts[i].trim();
            if (!isName(filter, false)) {
                throw new SqlTemplateValidationException(templateName,
                        "invalid filter '" + abbreviate(filter) + "' on placeholder " + name);
            }
            filters.add(filter);
        }
        return new Placeholder(name, List.copyOf(filters));
    }

    private static boolean isName(String s, boolean allowDot) {
        if (s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '_' || (allowDot && c == '.');
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private static String abbreviate(String s) {
        return s.length() <= 40 ? s : s.substring(0, 40) + "...";
    }
}
