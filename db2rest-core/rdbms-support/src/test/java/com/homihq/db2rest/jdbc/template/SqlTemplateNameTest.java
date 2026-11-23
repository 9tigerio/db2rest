package com.homihq.db2rest.jdbc.template;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SqlTemplateNameTest {

    @ParameterizedTest
    @ValueSource(strings = {"select_all", "a", "A1", "film-by-id", "report.v2", "x_y-z.1", "console", "con_x",
            "comx"})
    void acceptsLogicalNames(String name) {
        assertThat(SqlTemplateName.isValid(name)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            // traversal and separators
            "..", "../secret", "..\\secret", "a/../b", "nested/template", "nested\\template", "a..b",
            // absolute and OS-specific forms
            "/etc/passwd", "\\\\server\\share\\x", "C:\\Windows\\x", "C:x", "file:x", "~root", "a:stream",
            // encoded separators and dots
            "%2e%2e", "..%2fsecret", "a%2Fb", "a%5Cb", "%2Fetc%2Fpasswd",
            // hidden files, trailing dots, whitespace, control characters
            ".hidden", "_private", "-x", "name.", "name..", " name", "name ", "a b", "a\tb", "a\u0000b", "a\nb",
            // Windows device names
            "con", "CON", "nul", "aux.sql", "com1", "LPT9.txt", "prn",
            // non-ASCII look-alikes
            "a\u2215b", "a\uFF0Fb", "\uFF41", "caf\u00e9", "a\u2024\u2024b"
    })
    void rejectsEverythingElse(String name) {
        assertThat(SqlTemplateName.isValid(name)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {SqlTemplateName.MAX_LENGTH + 1, 10_000})
    void rejectsLongNames(int length) {
        assertThat(SqlTemplateName.isValid("a".repeat(length))).isFalse();
        assertThat(SqlTemplateName.isValid("a".repeat(SqlTemplateName.MAX_LENGTH))).isTrue();
    }
}
