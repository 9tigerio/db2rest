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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SchemaControllerTest {
    Map<String, DbMeta> metadataMap;
    JdbcManager jdbcManager;
    SchemaController schemaController;
    @BeforeEach
    void setUp() {
        List<DbTable> dbTableList = new ArrayList<>();
        List<DbColumn> dbColumns = new ArrayList<>();
        dbColumns.add(new DbColumn("tableName","name","alias",
                "tableAlias",true,"columnDataType",false,false,
                null,"coverChar","jsonParts"));

        dbTableList.add(new DbTable("schema","name","fullName",
                "alias",dbColumns,"type","coverChar"));

        metadataMap = new HashMap<>();
        metadataMap.put("key",new DbMeta("productName",2,"driverName",
                "driverVersion",dbTableList));
        jdbcManager = Mockito.mock(JdbcManager.class);
        schemaController = new SchemaController(jdbcManager);
    }

    @Test
    void getObjectsNoFilter() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> actualTableObject = schemaController.getObjects("key",null);
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        schemaController.getObjects("key",null);
        assertEquals(expectedTableObject,actualTableObject);
    }
    @Test
    void testGetObjectsFilterInvalid() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        Throwable exception = assertThrows(GenericDataAccessException.class,() -> schemaController.getObjects("key","invalidFilter"));
        assertEquals("Invalid filter condition. Only == supported for schema filter using a single value only.",exception.getMessage());
    }
    @Test
    void testGetObjectsValidFilter_schema(){
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        List<TableObject> actualTableObject = schemaController.getObjects("key","schema==schema");
        assertEquals(expectedTableObject,actualTableObject);

        actualTableObject = schemaController.getObjects("key","schema==notPresentInList");
        assertEquals(0,actualTableObject.size());
    }

    @Test
    void testGetObjectsValidFilter_name() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        List<TableObject> actualTableObject = schemaController.getObjects("key","name==name");
        assertEquals(expectedTableObject,actualTableObject);

        actualTableObject = schemaController.getObjects("key","name==notPresentInList");
        assertEquals(0,actualTableObject.size());
    }
    @Test
    void testGetObjectsValidFilter_type() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> expectedTableObject = metadataMap.get("key").dbTables().stream().map(TableObject::new).toList();
        List<TableObject> actualTableObject = schemaController.getObjects("key","type==type");
        assertEquals(expectedTableObject,actualTableObject);

        actualTableObject = schemaController.getObjects("key","type==notPresentInList");
        assertEquals(0,actualTableObject.size());
    }
    @Test
    void testGetObjectsValidFilter_invalidFieldFilter() {
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(metadataMap.get("key"));
        List<TableObject> actualTableObject;
        actualTableObject = schemaController.getObjects("key","invalidFilterField==notPresentInList");
        assertEquals(0,actualTableObject.size());
    }
    @Test
    void testGetObjectsDbIDInvalid(){
        when(jdbcManager.getDbMetaByDbId("key")).thenReturn(null);
        assertEquals(0,schemaController.getObjects("key","filter").size());

    }
}