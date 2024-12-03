package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(501)
@TestWithResources
class MsSQLExistsControllerTest extends MsSQLBaseIntegrationTest {

    @GivenJsonResource(TEST_JSON_FOLDER + "/INNER_JOIN_MULTI_TABLE.json")
    List<Map<String, Object>> innerJoinMultiTable;

    @Test
    @DisplayName("Actor exists by first_name")
    void existsActorByFirstName() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/actor/exists")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name==JENNIFER"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(true)))
                .andDo(document(DB_NAME + "-actor-exists-by-first_name"));
    }

    @Test
    @DisplayName("Actor exists with unknown name")
    void existsByUnknownName() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/actor/exists")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name==Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(false)))
                .andDo(document(DB_NAME + "-actor-exists-by-unknown-name"));
    }

    @Test
    @DisplayName("Actor exists with inner name")
    void existsInnerJoin() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/exists/_expand")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerJoinMultiTable))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(true)))

                .andDo(document(DB_NAME + "-exists-inner-join"));
    }

    @Test
    @DisplayName("Actor exists with inner name")
    void existsInnerJoinWithFilter() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/exists/_expand")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerJoinMultiTable))
                        .param("filter", "film_id==2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(false)))

                .andDo(document(DB_NAME + "-exists-inner-join-with-filter"));
    }

}
