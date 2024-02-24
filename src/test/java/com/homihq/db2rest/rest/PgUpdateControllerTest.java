package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.homihq.db2rest.utils.ITestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgUpdateControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Update an existing film")
    void updateExistingFilm() throws Exception {

        mockMvc.perform(patch("/film")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "public")
                        .param("filter", "title==\"ACADEMY DINOSAUR\"")
                        .content(ITestUtil.UPDATE_FILM_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(print())
                .andDo(document("pg-update-existing-film"));
    }

    @Test
    @DisplayName("Update a non-existing film")
    void updateNonExistingFilm() throws Exception {

        mockMvc.perform(patch("/film")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "public")
                        .param("filter", "title==\"BAAHUBALI\"")
                        .content(ITestUtil.UPDATE_NON_EXISTING_FILM_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(print())
                .andDo(document("pg-update-non-existing-film"));
    }

    @Test
    @DisplayName("Update non-existing table")
    void updateNonExistingTable() throws Exception {

        mockMvc.perform(patch("/unknown_table")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "sample_col==\"sample value 1\"")
                        .content(ITestUtil.UPDATE_NON_EXISTING_TABLE))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document("pg-update-non-existing-table"));
    }

    @Test
    @DisplayName("Updating multiple films")
    void updateMultipleColumns() throws Exception {

        mockMvc.perform(patch("/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "rating==\"G\"")
                        .header("Content-Profile", "public")
                        .content(ITestUtil.UPDATE_FILMS_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(print())
                .andDo(document("pg-update-multiple-films"));
    }

}
