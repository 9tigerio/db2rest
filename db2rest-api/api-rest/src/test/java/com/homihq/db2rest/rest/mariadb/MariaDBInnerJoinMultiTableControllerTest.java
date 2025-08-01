package com.homihq.db2rest.rest.mariadb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(313)
@TestWithResources
class MariaDBInnerJoinMultiTableControllerTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_JOIN_MULTI_TABLE.json")
    List<Map<String, Object>> INNER_JOIN_MULTI_TABLE;

    @Test
    @DisplayName("Test inner multi-table Join")
    void testInnerMultiTable() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/film/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INNER_JOIN_MULTI_TABLE))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].*", hasSize(17)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].language_id", equalTo(1)))
                .andExpect(jsonPath("$[0].actor_id", equalTo(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[0].last_name", equalTo("GUINESS")))
                .andDo(document("mariadb-inner-multi-table-join"));
    }
}
