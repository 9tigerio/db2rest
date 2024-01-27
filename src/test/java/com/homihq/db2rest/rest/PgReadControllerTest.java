package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgReadControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @Disabled  // TODO: Need to fix
    @DisplayName("C")
    void findAllFilms() throws Exception {

        mockMvc.perform(get("/film").header("Accept-Profile", "public")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
               // .andDo(print())
                .andDo(document("pg-get-all-films"));
    }

    @Test
    @DisplayName("Query returns single result")
    void query_returns_single_result() throws Exception {
        var json = """ 
                       {
                             "sql": "SELECT FIRST_NAME,LAST_NAME FROM ACTOR WHERE ACTOR_ID = :id",
                             "params" : {
                                 "id" : 1
                             },
                             "single" : true
                         }
                """;

        mockMvc.perform(post("/query").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", equalTo("PENELOPE")))
                //.andDo(print())
                .andDo(document("pg-create-a-film"));
    }

    @Test
    @DisplayName("Query returns list of results")
    void query_returns_list_of_results() throws Exception {
        var json = """ 
                       {
                           "sql": "SELECT FIRST_NAME,LAST_NAME FROM ACTOR WHERE ACTOR_ID IN (:id1, :id2)",
                           "params" : {
                               "id1" : 1,
                               "id2" : 2
                           },
                           "single" : false
                       }
                """;

        mockMvc.perform(post("/query").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
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
        var json = """ 
                       {
                           "sql": "",
                           "params" : {
                               "id1" : 1
                           },
                           "single" : false
                       }
                """;

        mockMvc.perform(post("/query").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
               // .andDo(print())
                .andDo(document("pg-create-a-film"));
    }

    @Test
    @DisplayName("Get count")
    void findFilmCount() throws Exception {
        mockMvc.perform(get("/film/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
              //  .andDo(print())
                .andDo(document("pg-get-film-count"));

    }

    @Test
    @DisplayName("Get one")
    void findOneFilm() throws Exception {
        mockMvc.perform(get("/film/one").accept(MediaType.APPLICATION_JSON)
                        .param("select", "title"))
                .andExpect(status().isOk())
               // .andDo(print())
                .andDo(document("pg-get-on-film"));
    }
}
