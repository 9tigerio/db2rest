package com.homihq.db2rest.jdbc.rest.meta.db;

import com.homihq.db2rest.BaseIntegrationTest;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
class DbInfoControllerIntegrationTest extends BaseIntegrationTest {

    Map<String, DbMeta> metadataMap;
    @MockBean
    JdbcManager jdbcManager;

    DbInfoController dbInfoController;

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
        metadataMap.put("1", new DbMeta("productName", 2, "driverName",
                "driverVersion", dbTableList));
        dbInfoController = new DbInfoController(jdbcManager);
        when(jdbcManager.getDbMetaMap()).thenReturn(metadataMap);
    }


    @Test
    void getObjects() throws Exception {
        mockMvc.perform(get(VERSION + "/$dbs")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].dbId").value("1"))  // Validate dbId field of the first object
                .andExpect(jsonPath("$[0].productName").value("productName"))  // Validate productName field
                .andExpect(jsonPath("$[0].majorVersion").value(2))  // Validate majorVersion field
                .andExpect(jsonPath("$[0].driverName").value("driverName"))  // Validate driverName field
                .andExpect(jsonPath("$[0].driverVersion").value("driverVersion"));
    }
}
