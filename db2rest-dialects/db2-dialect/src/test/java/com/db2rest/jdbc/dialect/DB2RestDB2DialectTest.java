package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.db2rest.jdbc.dialect.model.Database;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DB2RestDB2DialectTest {

    private DB2RestDB2Dialect db2Dialect;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        db2Dialect = new DB2RestDB2Dialect(objectMapper);
    }

    @Test
    @DisplayName("Should support DB2 database")
    void testIsSupportedDb() {
        assertTrue(db2Dialect.isSupportedDb("DB2/UDB", 11));
        assertTrue(db2Dialect.isSupportedDb("DB2", 11));
        assertTrue(db2Dialect.isSupportedDb("UDB", 11));
        assertFalse(db2Dialect.isSupportedDb("MySQL", 8));
        assertFalse(db2Dialect.isSupportedDb("PostgreSQL", 13));
    }

    @Test
    @DisplayName("Should render table name with schema and alias")
    void testRenderTableName() {
        DbTable table = mock(DbTable.class);
        when(table.schema()).thenReturn("TESTSCHEMA");
        when(table.name()).thenReturn("TESTTABLE");
        when(table.alias()).thenReturn("t1");

        String result = db2Dialect.renderTableName(table, false, false);
        assertEquals("\"TESTSCHEMA\".\"TESTTABLE\" t1", result);
    }

    @Test
    @DisplayName("Should render table name without alias for delete operations")
    void testRenderTableNameForDelete() {
        DbTable table = mock(DbTable.class);
        when(table.schema()).thenReturn("TESTSCHEMA");
        when(table.name()).thenReturn("TESTTABLE");

        String result = db2Dialect.renderTableName(table, false, true);
        assertEquals("\"TESTSCHEMA\".\"TESTTABLE\"", result);
    }

    @Test
    @DisplayName("Should render table name without alias")
    void testRenderTableNameWithoutAlias() {
        DbTable table = mock(DbTable.class);
        when(table.schema()).thenReturn("TESTSCHEMA");
        when(table.name()).thenReturn("TESTTABLE");

        String result = db2Dialect.renderTableNameWithoutAlias(table);
        assertEquals("\"TESTSCHEMA\".\"TESTTABLE\"", result);
    }

    @Test
    @DisplayName("Should process timestamp data types")
    void testProcessTypesWithTimestamp() {
        DbTable table = mock(DbTable.class);
        when(table.getColumnDataTypeName("created_at")).thenReturn("TIMESTAMP");

        List<String> columns = Arrays.asList("created_at");
        Map<String, Object> data = new HashMap<>();
        data.put("created_at", "2023-01-01T10:00:00");

        db2Dialect.processTypes(table, columns, data);

        assertTrue(data.get("created_at") instanceof LocalDateTime);
    }

    @Test
    @DisplayName("Should process JSON data types")
    void testProcessTypesWithJson() throws Exception {
        DbTable table = mock(DbTable.class);
        when(table.getColumnDataTypeName("json_data")).thenReturn("JSON");

        List<String> columns = Arrays.asList("json_data");
        Map<String, Object> data = new HashMap<>();
        Map<String, String> jsonObject = new HashMap<>();
        jsonObject.put("key", "value");
        data.put("json_data", jsonObject);

        db2Dialect.processTypes(table, columns, data);

        assertTrue(data.get("json_data") instanceof String);
        assertTrue(((String) data.get("json_data")).contains("\"key\":\"value\""));
    }

    @Test
    @DisplayName("Should convert timestamp string to LocalDateTime")
    void testConvertTimestamp() {
        String timestamp = "2023-01-01T10:00:00";
        LocalDateTime result = db2Dialect.convertTimestamp(timestamp);
        
        assertNotNull(result);
        assertEquals(2023, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(1, result.getDayOfMonth());
        assertEquals(10, result.getHour());
    }

    @Test
    @DisplayName("Should support batch return keys")
    void testSupportBatchReturnKeys() {
        assertTrue(db2Dialect.supportBatchReturnKeys());
    }

    @Test
    @DisplayName("Should return correct SQL templates")
    void testSqlTemplates() {
        assertEquals("read", db2Dialect.getReadSqlTemplate());
        assertEquals("delete", db2Dialect.getDeleteSqlTemplate());
        assertEquals("update", db2Dialect.getUpdateSqlTemplate());
        assertEquals("insert", db2Dialect.getInsertSqlTemplate());
        assertEquals("count", db2Dialect.getCountSqlTemplate());
        assertEquals("exists", db2Dialect.getExistSqlTemplate());
        assertEquals("find-one", db2Dialect.getFindOneSqlTemplate());
    }
}
