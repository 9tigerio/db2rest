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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(210)
@TestWithResources
@Disabled
class OracleBasicJoinControllerTest extends OracleBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/LEFT_JOIN_ORACLE.json")
    List<Map<String, Object>> leftJoin;

    @GivenJsonResource("/testdata/RIGHT_JOIN_ORACLE.json")
    List<Map<String, Object>> rightJoin;

    @Test
    @DisplayName("Test left Join")
    void testLeftJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/USERS/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(leftJoin))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].AUID", equalTo(1)))
                .andExpect(jsonPath("$[0].APID", equalTo(1)))
                .andExpect(jsonPath("$[1].AUID", equalTo(6)))
                .andExpect(jsonPath("$[1].APID", nullValue()))
                .andExpect(jsonPath("$[3].AUID", equalTo(4)))
                .andExpect(jsonPath("$[3].APID", nullValue()))
                .andExpect(jsonPath("$[3].FIRSTNAME", nullValue()))
                .andDo(document("oracle-left-join"));
    }

    @Test
    @DisplayName("Test right Join")
    void testRightJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/USERS/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rightJoin))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(10)))
                .andExpect(jsonPath("$[0].AUID", equalTo(1)))
                .andExpect(jsonPath("$[0].APID", equalTo(1)))
                .andExpect(jsonPath("$[0].USERNAME", equalTo("admin")))
                .andExpect(jsonPath("$[0].FIRSTNAME", equalTo("Jack")))

                .andExpect(jsonPath("$[1].AUID", equalTo(7)))
                .andExpect(jsonPath("$[1].APID", equalTo(7)))
                .andExpect(jsonPath("$[1].USERNAME", nullValue()))
                .andExpect(jsonPath("$[1].FIRSTNAME", equalTo("Ivan")))

                .andExpect(jsonPath("$[3].AUID", equalTo(3)))
                .andExpect(jsonPath("$[3].APID", equalTo(2)))
                .andExpect(jsonPath("$[3].USERNAME", nullValue()))
                .andExpect(jsonPath("$[3].FIRSTNAME", equalTo("Tom")))

                .andDo(document("oracle-right-join"));
    }
}
