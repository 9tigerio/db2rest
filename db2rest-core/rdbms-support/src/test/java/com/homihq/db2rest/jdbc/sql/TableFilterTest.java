package com.homihq.db2rest.jdbc.sql;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TableFilterTest {

    @Test
    void includesEverythingWithoutPatterns() {
        assertThat(TableFilter.of(null).includes("public", "film")).isTrue();
        assertThat(TableFilter.of(List.of()).includes("public", "film")).isTrue();
        assertThat(TableFilter.of(List.of("", "  ")).includesAll()).isTrue();
    }

    @Test
    void matchesTableNamePatternsInAnySchema() {
        TableFilter filter = TableFilter.of(List.of("film*", "actor"));

        assertThat(filter.includes("public", "film")).isTrue();
        assertThat(filter.includes("public", "film_actor")).isTrue();
        assertThat(filter.includes("stg", "film_text")).isTrue();
        assertThat(filter.includes("public", "actor")).isTrue();
        assertThat(filter.includes("public", "actor_info")).isFalse();
        assertThat(filter.includes("public", "customer")).isFalse();
    }

    @Test
    void matchesSchemaQualifiedPatterns() {
        TableFilter filter = TableFilter.of(List.of("sales.order_*"));

        assertThat(filter.includes("sales", "order_items")).isTrue();
        assertThat(filter.includes("archive", "order_items")).isFalse();
        assertThat(filter.includes(null, "order_items")).isFalse();
    }

    @Test
    void ignoresCaseAndSurroundingSpaces() {
        TableFilter filter = TableFilter.of(Arrays.asList(" Film* ", "SALES.Orders"));

        assertThat(filter.includes("PUBLIC", "FILM_ACTOR")).isTrue();
        assertThat(filter.includes("sales", "orders")).isTrue();
    }

    @Test
    void supportsSingleCharacterWildcard() {
        TableFilter filter = TableFilter.of(List.of("t_?"));

        assertThat(filter.includes("public", "t_1")).isTrue();
        assertThat(filter.includes("public", "t_10")).isFalse();
    }
}
