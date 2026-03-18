package com.homihq.db2rest.jdbc.sql;

import org.junit.jupiter.api.Test;
import java.sql.Types;
import static org.assertj.core.api.Assertions.assertThat;

class SqlTypesTest {

    @Test
    void shouldReturnTrueForNumericTypes() {
        assertThat(SqlTypes.isNumericType(Types.INTEGER)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.BIGINT)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.DECIMAL)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.NUMERIC)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.DOUBLE)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.FLOAT)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.REAL)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.SMALLINT)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.TINYINT)).isTrue();
        assertThat(SqlTypes.isNumericType(Types.BIT)).isTrue();
    }

    @Test
    void shouldReturnFalseForNonNumericTypes() {
        assertThat(SqlTypes.isNumericType(Types.VARCHAR)).isFalse();
        assertThat(SqlTypes.isNumericType(Types.DATE)).isFalse();
        assertThat(SqlTypes.isNumericType(Types.TIMESTAMP)).isFalse();
    }

    @Test
    void shouldReturnTrueForCharacterTypes() {
        assertThat(SqlTypes.isCharacterType(Types.CHAR)).isTrue();
        assertThat(SqlTypes.isCharacterType(Types.VARCHAR)).isTrue();
        assertThat(SqlTypes.isCharacterType(Types.LONGVARCHAR)).isTrue();
        assertThat(SqlTypes.isCharacterType(Types.NCHAR)).isTrue();
        assertThat(SqlTypes.isCharacterType(Types.NVARCHAR)).isTrue();
        assertThat(SqlTypes.isCharacterType(Types.LONGNVARCHAR)).isTrue();
    }

    @Test
    void shouldReturnFalseForNonCharacterTypes() {
        assertThat(SqlTypes.isCharacterType(Types.INTEGER)).isFalse();
        assertThat(SqlTypes.isCharacterType(Types.BLOB)).isFalse();
        assertThat(SqlTypes.isCharacterType(Types.DATE)).isFalse();
    }

    @Test
    void shouldReturnTrueForCharacterOrClobTypes() {
        assertThat(SqlTypes.isCharacterOrClobType(Types.CHAR)).isTrue();
        assertThat(SqlTypes.isCharacterOrClobType(Types.VARCHAR)).isTrue();
        assertThat(SqlTypes.isCharacterOrClobType(Types.CLOB)).isTrue();
        assertThat(SqlTypes.isCharacterOrClobType(Types.NCLOB)).isTrue();
    }

    @Test
    void shouldReturnFalseForNonCharacterOrClobTypes() {
        assertThat(SqlTypes.isCharacterOrClobType(Types.INTEGER)).isFalse();
        assertThat(SqlTypes.isCharacterOrClobType(Types.BLOB)).isFalse();
    }
}