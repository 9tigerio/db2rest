package com.homihq.db2rest.jdbc.rest.meta.schema;

import com.homihq.db2rest.BaseIntegrationTest;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SchemaControllerIntegrationTest extends BaseIntegrationTest {

    Map<String, DbMeta> metadataMap;
    @MockBean
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
        schemaController = new SchemaController(jdbcManager);
        when(jdbcManager.getDbMetaByDbId(anyString())).thenReturn(metadataMap.get("key"));
    }

    /**
     * This test tests the use case where we request for all schemas without
     * providing any filter.
     * @throws Exception
     */
    @Test
    void testGetObjects() throws Exception {
        mockMvc.perform(get(VERSION + "/1/$schemas")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                //.andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].*", hasSize(3)))
                .andDo(document("schema-no-filter"));
    }

    /**
     * This test tests the use case where we are sending an invalid filter
     * i.e, we are sending a filter which is not valid.
     * @throws Exception
     */
    @Test
    void testGetObjectsInvalidFilter() throws Exception {
        mockMvc.perform(get(VERSION + "/1/$schemas")
                        .param("filter","name=employee")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.type").value("https://db2rest.com/error/generic-error"))
                .andExpect(jsonPath("$.title").value("Generic Data Access Error"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Invalid filter condition. Only == supported for schema filter using a single value only."))
                .andExpect(jsonPath("$.instance").value("/v1/rdbms/1/$schemas"))
                .andExpect(jsonPath("$.errorCategory").value("Data-access-error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(document("schema-invalid-filter"));
    }

    /**
     * Tests response if there is a filter, and it is a valid filter.
     * Valid filters include
     * 1. == sign in between filter field and filter value
     * 2. A string to the left of == and a string value to the right of it.
     * @throws Exception
     */
    @Test
    void testGetObjectsValidFilter()  throws Exception {
        mockMvc.perform(get("/v1/rdbms/1/$schemas")
                        .param("filter", "schema==schema")
                        .contentType("application/json"))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$").isArray())  // Ensure it's an array
                .andExpect(jsonPath("$[0].schema").value("schema"))  // Validate the first object's schema field
                .andExpect(jsonPath("$[0].name").value("name"))  // Validate the name field
                .andExpect(jsonPath("$[0].type").value("type"))
                .andDo(document("schema-valid-filter"));
    }

    /**
     * This test checks for the use case where we send a request to /$schemas,
     * but we do not have a valid filter field.
     * NOTE: In this use case, the filter in itself is valid because it contains ==.
     *  Currently, we have filters based on schema, type and name.
     * This test checks the response and ensures it is empty if the filter argument
     * is not in (schema,type, name)
     * @throws Exception
     */
    @Test
    void testGetObjectsInvalidFilterField()  throws Exception {
        mockMvc.perform(get("/v1/rdbms/1/$schemas")
                        .param("filter", "someUnknownFilter==schema")
                        .contentType("application/json"))
                .andExpect(status().isOk())  // Expect 200 OK
                .andExpect(jsonPath("$").isArray())  // Ensure it's an array
                .andExpect(jsonPath("$").isEmpty()) //Ensure it is empty
                .andDo(document("schema-unknown-filter"));
    }
}