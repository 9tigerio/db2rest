package com.homihq.db2rest.jdbc.template;

import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static com.homihq.db2rest.jdbc.template.SqlTemplateOperation.DELETE;
import static com.homihq.db2rest.jdbc.template.SqlTemplateOperation.INSERT;
import static com.homihq.db2rest.jdbc.template.SqlTemplateOperation.QUERY;
import static com.homihq.db2rest.jdbc.template.SqlTemplateOperation.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlTemplateOperationTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "SELECT * FROM film|QUERY",
            "select 1|QUERY",
            "WITH x AS (SELECT 1) SELECT * FROM x|QUERY",
            "VALUES (1)|QUERY",
            "(SELECT 1) UNION (SELECT 2)|QUERY",
            "INSERT INTO t VALUES (1)|INSERT",
            "update film SET a = 1|UPDATE",
            "DELETE FROM t|DELETE",
    })
    void detectsFirstKeyword(String sql, SqlTemplateOperation expected) {
        assertThat(SqlTemplateOperation.detect(sql)).contains(expected);
    }

    @Test
    void skipsWhitespaceAndComments() {
        assertThat(SqlTemplateOperation.detect("\n\n   \tDELETE\nFROM t")).contains(DELETE);
        assertThat(SqlTemplateOperation.detect("-- remove old rows\nDELETE FROM t")).contains(DELETE);
        assertThat(SqlTemplateOperation.detect("/* note */ /* more\n */ UPDATE t SET a = 1")).contains(UPDATE);
        assertThat(SqlTemplateOperation.detect("\r\n-- a\r\n-- b\r\ninsert into t values (1)")).contains(INSERT);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "-- only a comment", "/* unterminated", "MERGE INTO t", "CALL proc()",
            "TRUNCATE t", "DROP TABLE t", "selection", "deleted", "123"})
    void unknownKeywordsAreNotGuessed(String sql) {
        assertThat(SqlTemplateOperation.detect(sql)).isEmpty();
        assertThatThrownBy(() -> SqlTemplateOperation.resolve("t", Optional.empty(), sql))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("db2rest:operation");
    }

    @Test
    void readsDeclarationFromLeadingComments() {
        assertThat(SqlTemplateOperation.declaredIn("t", "-- db2rest:operation=update\nWITH x AS (...) UPDATE t"))
                .contains(UPDATE);
        assertThat(SqlTemplateOperation.declaredIn("t", "\n-- owner: reporting\n--   db2rest:operation = QUERY  \nCALL p()"))
                .contains(QUERY);
        assertThat(SqlTemplateOperation.declaredIn("t", "-- db2rest:operation=insert\r\nMERGE INTO t"))
                .contains(INSERT);
    }

    @Test
    void ignoresDeclarationAfterFirstStatementLine() {
        assertThat(SqlTemplateOperation.declaredIn("t", "SELECT 1\n-- db2rest:operation=delete")).isEmpty();
        assertThat(SqlTemplateOperation.declaredIn("t", "SELECT 1")).isEmpty();
        assertThat(SqlTemplateOperation.declaredIn("t", "")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-- db2rest:operation=drop\nDROP TABLE t", "-- db2rest:operation update\nUPDATE t",
            "-- db2rest:operation=\nUPDATE t"})
    void rejectsBadDeclarations(String source) {
        assertThatThrownBy(() -> SqlTemplateOperation.declaredIn("t", source))
                .isInstanceOf(SqlTemplateValidationException.class);
    }

    @Test
    void declarationWinsForAmbiguousStatements() {
        assertThat(SqlTemplateOperation.resolve("t", Optional.of(DELETE), "WITH x AS (SELECT 1) DELETE FROM t"))
                .isEqualTo(DELETE);
        assertThat(SqlTemplateOperation.resolve("t", Optional.of(UPDATE), "MERGE INTO t USING s ON (1=1)"))
                .isEqualTo(UPDATE);
    }

    @Test
    void declarationMayNotContradictAWriteKeyword() {
        assertThatThrownBy(() -> SqlTemplateOperation.resolve("t", Optional.of(QUERY), "DELETE FROM t"))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("does not match");
        assertThatThrownBy(() -> SqlTemplateOperation.resolve("t", Optional.of(INSERT), "UPDATE t SET a = 1"))
                .isInstanceOf(SqlTemplateValidationException.class);
    }
}
