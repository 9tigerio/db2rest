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

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(312)
@TestWithResources
class MariaDBInnerJoinControllerTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_JOIN.json")
    List<Map<String, Object>> innerJoin;

    @Test
    @DisplayName("Test inner Join")
    void testInnerJoin() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/review/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerJoin))
                )
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(3))))
                .andExpect(jsonPath("$[0].*", hasSize(7)))
                //.andExpect(jsonPath("$[0].review_id", equalTo("ABC123")))
                //.andExpect(jsonPath("$[0].film_id", equalTo(1)))
                //.andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("mariadb-inner-join"));
    }
}
