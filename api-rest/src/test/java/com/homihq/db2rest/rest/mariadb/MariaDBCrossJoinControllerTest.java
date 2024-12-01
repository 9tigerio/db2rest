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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(311)
@TestWithResources
class MariaDBCrossJoinControllerTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CROSS_JOIN_USERS.json")
    List<Map<String, Object>> crossJoin;

    @GivenJsonResource("/testdata/CROSS_JOIN_TOPS.json")
    List<Map<String, Object>> crossJoinTops;

    @Test
    @DisplayName("Test cross Join - Users")
    void testCrossJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/users/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crossJoin))
                )
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(16)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].auid", equalTo(1)))
                .andExpect(jsonPath("$[0].apid", equalTo(1)))
                .andExpect(jsonPath("$[0].firstname", equalTo("Jack")))

                .andExpect(jsonPath("$[1].auid", equalTo(2)))
                .andExpect(jsonPath("$[1].apid", equalTo(1)))
                .andExpect(jsonPath("$[1].firstname", equalTo("Jack")))

                .andDo(document("mariadb-cross-join-users"));
    }

    @Test
    @DisplayName("Test cross Join - Tops")
    void testCrossJoinTops() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/tops/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crossJoinTops))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(9)))
                .andExpect(jsonPath("$[0].*", hasSize(6)))

                .andExpect(jsonPath("$[0].top_item", equalTo("sweater")))
                .andExpect(jsonPath("$[0].bottom_item", equalTo("jeans")))
                .andExpect(jsonPath("$[0].color", equalTo("red")))
                .andExpect(jsonPath("$[0].botColor", equalTo("blue")))
                .andDo(document("mariadb-cross-join-tops"));
    }

}
