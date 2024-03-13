package com.homihq.db2rest.rest;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
@Disabled
class MySQLInnerSelfJoinControllerTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/INNER_SELF_JOIN.json")
    List<Map<String,Object>> INNER_SELF_JOIN;

    @Test
    @DisplayName("Test inner self Join")
    @Disabled
    void testInnerSelfJoin() throws Exception {


        mockMvc.perform(post("/film/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INNER_SELF_JOIN))
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


                .andDo(document("mysql-inner-multi-table-join"));


    }



}
