package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PgUpdateControllerTest extends PostgreSQLBaseIntegrationTest {
    @Test
    @DisplayName("Update an existing film")
    public void updateExistingFilm() throws Exception {

        var json = """
                {
                    "rating": "NC-17"
                }
                """;

        mockMvc.perform(patch("/film")
                        .param("filter", "title==\"ACADEMY DINOSAUR\"")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "sakila")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(1)))
                .andDo(print())
                .andDo(document("pg-update-existing-film"));
    }

    @Test
    @DisplayName("Update a non-existing film")
    public void updateNonExistingFilm() throws Exception {

        var json = """
                {
                    "special_features": "CGI,Fantasy,Action"
                }
                """;

        mockMvc.perform(patch("/film")
                        .param("filter", "title==\"BAAHUBALI\"")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "sakila")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(0)))
                .andDo(print())
                .andDo(document("pg-update-non-existing-film"));
    }

    @Test
    @DisplayName("Update non-existing table")
    public void updateNonExistingTable() throws Exception {
        var json = """
                {
                    "sample_col": "sample value"
                }
                """;

        mockMvc.perform(patch("/unnamed_table")
                        .param("filter", "sample_col==\"sample value 1\"")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "sakila")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-update-non-existing-table"));
    }

    @Test
    @DisplayName("Updating multiple films")
    public void updateMultipleColumns() throws Exception {
        var json = """
                {
                    "rating": "PG"
                }
                """;

        mockMvc.perform(patch("/film")
                        .param("filter", "rating==\"G\"")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "sakila")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(2)))
                .andDo(print())
                .andDo(document("pg-update-multiple-films"));
    }

}
