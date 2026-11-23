package com.homihq.db2rest.jdbc.template;

import java.util.Locale;
import java.util.Set;

/**
 * Grammar for the public template identifier used in {@code /v1/rdbms/{dbId}/sql/{name}}.
 * <p>
 * A template name is a logical identifier, not a path. It is accepted only if it
 * <ul>
 *   <li>is 1 to {@value #MAX_LENGTH} characters long,</li>
 *   <li>starts with an ASCII letter or digit,</li>
 *   <li>otherwise contains only ASCII letters, digits, {@code _}, {@code -} and {@code .},</li>
 *   <li>has no {@code ..} and does not end with {@code .},</li>
 *   <li>is not a Windows device name such as {@code CON} or {@code COM1} (with or without
 *       an extension).</li>
 * </ul>
 * The name maps to exactly one file, {@code <name>.sql}, directly inside the configured
 * template directory. Separators, drive letters, UNC prefixes, percent-encoding and
 * non-ASCII look-alike characters are all rejected by the character rule.
 */
public final class SqlTemplateName {

    public static final int MAX_LENGTH = 128;

    private static final Set<String> WINDOWS_DEVICE_NAMES = Set.of(
            "con", "prn", "aux", "nul",
            "com1", "com2", "com3", "com4", "com5", "com6", "com7", "com8", "com9",
            "lpt1", "lpt2", "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9");

    private SqlTemplateName() {
    }

    public static boolean isValid(String name) {
        if (name == null || name.isEmpty() || name.length() > MAX_LENGTH) {
            return false;
        }
        if (!isAsciiLetterOrDigit(name.charAt(0)) || name.endsWith(".") || name.contains("..")) {
            return false;
        }
        for (int i = 1; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!isAsciiLetterOrDigit(c) && c != '_' && c != '-' && c != '.') {
                return false;
            }
        }
        int dot = name.indexOf('.');
        String base = (dot < 0 ? name : name.substring(0, dot)).toLowerCase(Locale.ROOT);
        return !WINDOWS_DEVICE_NAMES.contains(base);
    }

    private static boolean isAsciiLetterOrDigit(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }
}
