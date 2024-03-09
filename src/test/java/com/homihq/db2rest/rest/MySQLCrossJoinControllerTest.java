package com.homihq.db2rest.rest;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
class MySQLCrossJoinControllerTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CROSS_JOIN_USERS.json")
    List<Map<String,Object>> CROSS_JOIN;

    @GivenJsonResource("/testdata/CROSS_JOIN_TOPS.json")
    List<Map<String,Object>> CROSS_JOIN_TOPS;


    @Test
    @DisplayName("Test cross Join - Users")
    void testCrossJoin() throws Exception {


        mockMvc.perform(post("/users/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CROSS_JOIN))
                )
               // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(16)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].auid", equalTo(6)))
                .andExpect(jsonPath("$[0].apid", equalTo(1)))
                .andExpect(jsonPath("$[0].firstname", equalTo("Jack")))

                .andExpect(jsonPath("$[1].auid", equalTo(4)))
                .andExpect(jsonPath("$[1].apid", equalTo(1)))
                .andExpect(jsonPath("$[1].firstname", equalTo("Jack")))

                .andDo(document("mysql-cross-join-users"));


    }

    @Test
    @DisplayName("Test cross Join - Tops")
    void testCrossJoinTops() throws Exception {


        mockMvc.perform(post("/tops/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CROSS_JOIN_TOPS))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(9)))
                .andExpect(jsonPath("$[0].*", hasSize(6)))

                .andExpect(jsonPath("$[0].top_item", equalTo("tank_top")))
                .andExpect(jsonPath("$[0].bottom_item", equalTo("jeans")))
                .andExpect(jsonPath("$[0].color", equalTo("white")))
                .andExpect(jsonPath("$[0].botColor", equalTo("blue")))


                .andDo(document("mysql-cross-join-tops"));


    }

}
