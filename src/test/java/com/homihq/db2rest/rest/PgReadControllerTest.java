package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.homihq.db2rest.utils.ITestUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgReadControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @Disabled  // TODO: Need to fix
    @DisplayName("C")
    void findAllFilms() throws Exception {

        mockMvc.perform(get("/film")
                        .accept(APPLICATION_JSON)
                        .header("Accept-Profile", "public"))
                .andExpect(status().isOk())
                // .andDo(print())
                .andDo(document("pg-get-all-films"));
    }

    @Test
    @DisplayName("Query returns single result")
    void query_returns_single_result() throws Exception {

        mockMvc.perform(post("/query")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(ITestUtil.SINGLE_RESULT_ACTOR_QUERY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", equalTo("PENELOPE")))
                //.andDo(print())
                .andDo(document("pg-create-a-film"));
    }

    @Test
    @DisplayName("Query returns list of results")
    void query_returns_list_of_results() throws Exception {

        mockMvc.perform(post("/query")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(ITestUtil.BULK_RESULT_ACTOR_QUERY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[1].last_name", equalTo("WAHLBERG")))
                //.andDo(print())
                .andDo(document("pg-create-a-film"));
    }

    @Test
    @DisplayName("Query returns 400 bad request error")
    void query_returns_400_bad_request() throws Exception {

        mockMvc.perform(post("/query")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(ITestUtil.EMPTY_ACTOR_QUERY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                // .andDo(print())
                .andDo(document("pg-create-a-film"));
    }

    @Test
    @DisplayName("Get count")
    void findFilmCount() throws Exception {
        mockMvc.perform(get("/film/count")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                //  .andDo(print())
                .andDo(document("pg-get-film-count"));

    }

    @Test
    @DisplayName("Get one")
    void findOneFilm() throws Exception {
        mockMvc.perform(get("/film/one")
                        .accept(APPLICATION_JSON)
                        .param("select", "title"))
                .andExpect(status().isOk())
                // .andDo(print())
                .andDo(document("pg-get-on-film"));
    }
}
