package com.homihq.db2rest.rest.mariadb;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestWithResources
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(314)
class MariaDBInnerSelfJoinControllerTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_SELF_JOIN.json")
    List<Map<String, Object>> innerSelfJoin;

    @Test
    @DisplayName("Test inner self Join")
    void testInnerSelfJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/film/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerSelfJoin))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                //.andExpect(jsonPath("$.*", hasSize(1)))
                //.andExpect(jsonPath("$[0].*", hasSize(17)))
                //.andExpect(jsonPath("$[0].film_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].language_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].actor_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                //.andExpect(jsonPath("$[0].last_name", equalTo("GUINESS")))
                .andDo(document("mariadb-inner-multi-table-join"));
    }
}
