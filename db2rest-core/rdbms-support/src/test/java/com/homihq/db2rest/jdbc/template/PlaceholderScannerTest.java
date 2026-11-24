package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import com.homihq.db2rest.jdbc.dto.Placeholder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlaceholderScannerTest {

    @Test
    void replacesPlaceholdersWithNamedParameters() {
        var result = PlaceholderScanner.scan("t",
                "SELECT * FROM film WHERE film_id = {{ params.film_id | is_required }} AND rating = {{params.rating}}",
                10);

        assertThat(result.sql()).isEqualTo("SELECT * FROM film WHERE film_id = :params.film_id AND rating = :params.rating");
        assertThat(result.placeholders()).containsExactly(
                new Placeholder("params.film_id", List.of("is_required")),
                new Placeholder("params.rating", List.of()));
    }

    @Test
    void keepsEveryFilterInOrder() {
        var result = PlaceholderScanner.scan("t", "x = {{ content.id | is_required | is_uuid }}", 10);
        assertThat(result.placeholders()).containsExactly(
                new Placeholder("content.id", List.of("is_required", "is_uuid")));
    }

    @Test
    void handlesNewlinesInsidePlaceholder() {
        var result = PlaceholderScanner.scan("t", "a = {{\n  params.a\n  | is_required\n}}::integer", 10);
        assertThat(result.sql()).isEqualTo("a = :params.a::integer");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SELECT '{{1,2},{3,4}}'::int[]",
            "SELECT '{\"a\":{\"b\":1}}'::jsonb",
            "SELECT 1 -- {{ unterminated",
            "SELECT '}}' || '{{'",
            "no placeholders at all"
    })
    void leavesNonPlaceholderBracesAlone(String sql) {
        var result = PlaceholderScanner.scan("t", sql, 10);
        assertThat(result.sql()).isEqualTo(sql);
        assertThat(result.placeholders()).isEmpty();
    }

    @Test
    void findsPlaceholderAfterBraceNoise() {
        var result = PlaceholderScanner.scan("t", "'{{ a } b' = {{ params.b }}", 10);
        assertThat(result.sql()).isEqualTo("'{{ a } b' = :params.b");
        assertThat(result.placeholders()).extracting(Placeholder::namedParam).containsExactly("params.b");
    }

    @ParameterizedTest
    @ValueSource(strings = {"{{ }}", "{{ | is_required }}", "{{ a b }}", "{{ a; drop table x }}",
            "{{ params.x-y }}", "{{ a | }}", "{{ a || is_required }}", "{{ a | is required }}", "{{{ a }}"})
    void rejectsMalformedPlaceholders(String sql) {
        assertThatThrownBy(() -> PlaceholderScanner.scan("t", sql, 10))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageStartingWith("Template t rejected:");
    }

    @Test
    void enforcesPlaceholderLimit() {
        String sql = "{{ a }},".repeat(11);
        assertThat(PlaceholderScanner.scan("t", "{{ a }},".repeat(10), 10).placeholders()).hasSize(10);
        assertThatThrownBy(() -> PlaceholderScanner.scan("t", sql, 10))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("more than 10 placeholders");
    }

    /**
     * Inputs from CodeQL alert 9 (strings starting with '{{{{{{|' and many '||') and other
     * shapes that make backtracking patterns blow up. Each completes in well under a second.
     */
    @ParameterizedTest
    @ValueSource(ints = {1_000, 100_000, 1_000_000})
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void adversarialInputCompletesInLinearTime(int n) {
        scanQuietly("{{{{{{|" + "||".repeat(n));
        scanQuietly("{{{{{{|" + "||".repeat(n) + "}}");
        scanQuietly("{{ a |" + " |".repeat(n) + " }");
        scanQuietly("{{".repeat(n));
        scanQuietly("{{".repeat(n) + "}}");
        scanQuietly("{{}".repeat(n) + "}}");
        scanQuietly("}}".repeat(n));
        scanQuietly("{{ x }".repeat(n));
    }

    private static void scanQuietly(String sql) {
        try {
            PlaceholderScanner.scan("t", sql, Integer.MAX_VALUE);
        } catch (SqlTemplateValidationException expected) {
            // rejection is fine; what matters is that it returns quickly
        }
    }
}
