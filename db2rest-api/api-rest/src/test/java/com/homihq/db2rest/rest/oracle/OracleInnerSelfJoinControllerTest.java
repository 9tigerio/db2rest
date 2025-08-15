package com.homihq.db2rest.rest.oracle;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.OracleBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(214)
@Disabled
class OracleInnerSelfJoinControllerTest extends OracleBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_SELF_JOIN_ORACLE.json")
    List<Map<String, Object>> INNER_SELF_JOIN;

    @Test
    @DisplayName("Test inner self Join")
    void testInnerSelfJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILM/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INNER_SELF_JOIN))
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
                .andDo(document("oracle-inner-multi-table-join"));
    }
}
