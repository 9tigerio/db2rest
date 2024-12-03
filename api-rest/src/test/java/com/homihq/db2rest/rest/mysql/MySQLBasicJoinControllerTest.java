package com.homihq.db2rest.rest.mysql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(10)
@TestWithResources
class MySQLBasicJoinControllerTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/LEFT_JOIN.json")
    List<Map<String, Object>> leftJoin;

    @GivenJsonResource("/testdata/RIGHT_JOIN.json")
    List<Map<String, Object>> rightJoin;

    @Disabled("to fix")
    @Test
    @DisplayName("Test left Join")
    void testLeftJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mysqldb/users/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leftJoin))
                )
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].auid", equalTo(1)))
                .andExpect(jsonPath("$[0].apid", equalTo(1)))
                .andExpect(jsonPath("$[1].auid", equalTo(2)))
                .andExpect(jsonPath("$[1].apid", nullValue()))
                .andExpect(jsonPath("$[3].auid", equalTo(6)))
                .andExpect(jsonPath("$[3].apid", nullValue()))
                .andExpect(jsonPath("$[3].firstname", nullValue()))
                .andDo(document("mysql-left-join"));


    }

    @Test
    @DisplayName("Test right Join")
    void testRightJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mysqldb/users/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rightJoin))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].auid", equalTo(1)))
                .andExpect(jsonPath("$[0].apid", equalTo(1)))
                .andExpect(jsonPath("$[0].apid", equalTo(1)))
                .andExpect(jsonPath("$[0].firstname", equalTo("Jack")))

                .andExpect(jsonPath("$[1].auid", equalTo(3)))
                .andExpect(jsonPath("$[1].apid", equalTo(2)))
                .andExpect(jsonPath("$[1].username", nullValue()))
                .andExpect(jsonPath("$[1].firstname", equalTo("Tom")))

                .andExpect(jsonPath("$[3].auid", equalTo(7)))
                .andExpect(jsonPath("$[3].apid", equalTo(7)))
                .andExpect(jsonPath("$[3].username", nullValue()))
                .andExpect(jsonPath("$[3].firstname", equalTo("Ivan")))

                .andDo(document("mysql-right-join"));
    }
}
