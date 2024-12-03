package com.homihq.db2rest.rest.mysql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(14)
class MySQLInnerSelfJoinControllerTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_SELF_JOIN.json")
    List<Map<String, Object>> innerSelfJoin;

    @Test
    @DisplayName("Test inner self Join")
    void testInnerSelfJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mysqldb/film/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerSelfJoin))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                //.andExpect(jsonPath("$.*", hasSize(1)))
                //.andExpect(jsonPath("$[0].*", hasSize(17)))
                //.andExpect(jsonPath("$[0].film_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].language_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].actor_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                //.andExpect(jsonPath("$[0].last_name", equalTo("GUINESS")))
                .andDo(document("mysql-inner-multi-table-join"));
    }
}
