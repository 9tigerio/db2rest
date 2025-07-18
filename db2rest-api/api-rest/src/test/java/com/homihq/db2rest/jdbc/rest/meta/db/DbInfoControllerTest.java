package com.homihq.db2rest.jdbc.rest.meta.db;

import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import com.homihq.db2rest.rest.meta.db.DbInfoController;
import com.homihq.db2rest.rest.meta.db.DbInfoObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class DbInfoControllerTest {

    Map<String, DbMeta> metadataMap;

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
    }

    @Test
    void testGetObjects() {

        JdbcManager jdbcManagerMock = Mockito.mock(JdbcManager.class);
        when(jdbcManagerMock.getDbMetaMap()).thenReturn(metadataMap);
        DbInfoController dbInfoController = new DbInfoController(jdbcManagerMock);
        List<DbInfoObject> actualDbInfoList = dbInfoController.getObjects();
        List<DbInfoObject> expectedDbInfoList = new ArrayList<>();
        metadataMap.forEach(
                (k, v) -> expectedDbInfoList.add(new DbInfoObject(k, v.productName(), v.majorVersion(), v.driverName(), v.driverVersion()))
        );

        assertEquals(expectedDbInfoList, actualDbInfoList);
    }
}
