package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(503)
@TestWithResources
class MsSQLUpdateControllerTest extends MsSQLBaseIntegrationTest {

    @GivenJsonResource(TEST_JSON_FOLDER + "/UPDATE_FILM_REQUEST.json")
    Map<String, Object> updateFilmRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/UPDATE_NON_EXISTING_FILM_REQUEST.json")
    Map<String, Object> updateNonExistingFilmRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/UPDATE_NON_EXISTING_TABLE.json")
    Map<String, Object> updateNonExistingTable;

    @GivenJsonResource(TEST_JSON_FOLDER + "/UPDATE_FILMS_REQUEST.json")
    Map<String, Object> updateFilmsRequest;

    @Test
    @DisplayName("Update an existing film")
    void updateExistingFilm() throws Exception {
        mockMvc.perform(patch(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "title==\"ACADEMY DINOSAUR\"")
                        .content(objectMapper.writeValueAsString(updateFilmRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document(DB_NAME + "-update-existing-film"));
    }

    @Test
    @DisplayName("Update a non-existing film")
    void updateNonExistingFilm() throws Exception {
        mockMvc.perform(patch(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "title==\"BAAHUBALI\"")
                        .content(objectMapper.writeValueAsString(updateNonExistingFilmRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(print())
                .andDo(document(DB_NAME + "-update-non-existing-film"));
    }

    @Test
    @DisplayName("Update non-existing table")
    void updateNonExistingTable() throws Exception {
        mockMvc.perform(patch(getPrefixApiUrl() + "/unknown_table")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "sample_col==\"sample value 1\"")
                        .content(objectMapper.writeValueAsString(updateNonExistingTable))
                )
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document(DB_NAME + "-update-non-existing-table"));
    }

    @Test
    @DisplayName("Updating multiple films")
    void updateMultipleColumns() throws Exception {
        mockMvc.perform(patch(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "rating==\"G\"")
                        .content(objectMapper.writeValueAsString(updateFilmsRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document(DB_NAME + "-update-multiple-films"));
    }

}
