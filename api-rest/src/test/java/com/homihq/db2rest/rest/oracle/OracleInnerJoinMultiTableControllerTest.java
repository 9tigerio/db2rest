package com.homihq.db2rest.rest.oracle;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.OracleBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(213)
@TestWithResources
@Disabled
class OracleInnerJoinMultiTableControllerTest extends OracleBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_JOIN_MULTI_TABLE_ORACLE.json")
    List<Map<String, Object>> innerJoinMultiTable;

    @Test
    @DisplayName("Test inner multi-table Join")
    void testInnerMultiTable() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILM/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerJoinMultiTable))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].*", hasSize(17)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].LANGUAGE_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].ACTOR_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].FIRST_NAME", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[0].LAST_NAME", equalTo("GUINESS")))
                .andDo(document("oracle-inner-multi-table-join"));
    }

}
