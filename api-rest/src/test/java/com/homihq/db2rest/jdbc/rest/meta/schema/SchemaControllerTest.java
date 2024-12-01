package com.homihq.db2rest.jdbc.rest.meta.schema;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class SchemaControllerTest {
    Map<String, DbMeta> metadataMap;
    JdbcManager jdbcManager;
    SchemaController schemaController;

    @BeforeEach
    void setUp() {
        List<DbTable> dbTableList = new ArrayList<>();
        List<DbColumn> dbColumns = new ArrayList<>();
        dbColumns.add(new DbColumn("tableName", "name", "alias",
                "tableAlias", true, "columnDataType", false, false,
                null, "coverChar", "jsonParts"));

        dbTableList.add(new DbTable("schema", "name", "fullName",
                "alias", dbColumns, "type", "coverChar"));

        metadataMap = new HashMap<>();
        metadataMap.put("key", new DbMeta("productName", 2, "driverName",
                "driverVersion", dbTableList));
        jdbcManager = Mockito.mock(JdbcManager.class);
        schemaController = new SchemaController(jdbcManager);
    }

    private List<TableObject> getActualTableObject(String filter) {
        return (List<TableObject>) schemaController.getObjects("key", filter, Boolean.FALSE);
    }

    private void testFilter(String goodFilter, String badFilter) {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        List<TableObject> actualTableObject = getActualTableObject(goodFilter);
        assertEquals(expectedTableObject, actualTableObject);

        actualTableObject = getActualTableObject(badFilter);
        assertEquals(0, actualTableObject.size());
    }

    @Test
    void getObjectsNoFilter() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> actualTableObject = getActualTableObject(null);
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        assertEquals(expectedTableObject, actualTableObject);
    }

    @Test
    void testGetObjectsFilterInvalid() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        Throwable exception = assertThrows(GenericDataAccessException.class, () -> schemaController.getObjects("key", "invalidFilter", Boolean.FALSE));
        assertEquals("Invalid filter condition. Only == supported for schema filter using a single value only.", exception.getMessage());
    }

    @Test
    void testGetObjectsValidFilter_schema() {
        testFilter("schema==schema", "schema==notPresentInList");
    }

    @Test
    void testGetObjectsValidFilter_name() {
        testFilter("name==name", "name==notPresentInList");
    }

    @Test
    void testGetObjectsValidFilter_type() {
        testFilter("type==type", "type==notPresentInList");
    }

    @Test
    void testGetObjectsValidFilter_invalidFieldFilter() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> actualTableObject = getActualTableObject("invalidFilterField==notPresentInList");
        assertEquals(0, actualTableObject.size());
    }

    @Test
    void testGetObjectsDbIDInvalid() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(null);
        assertEquals(0, schemaController.getObjects("key", "filter", Boolean.FALSE).size());
    }

    @Test
    void testGetObjectsWithColumns() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableWithColumnsObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableWithColumnsObject::new).toList();
        List<TableWithColumnsObject> actualTableObject = (List<TableWithColumnsObject>) schemaController.getObjects("key", null, Boolean.TRUE);
        assertEquals(expectedTableObject, actualTableObject);
    }
}
