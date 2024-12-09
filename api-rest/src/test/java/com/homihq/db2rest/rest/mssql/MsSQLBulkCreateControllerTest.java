package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.GivenTextResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(503)
@TestWithResources
class MsSQLBulkCreateControllerTest extends MsSQLBaseIntegrationTest {

    @GivenJsonResource(TEST_JSON_FOLDER + "/BULK_CREATE_FILM_REQUEST.json")
    List<Map<String, Object>> bulkCreateFilmRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/BULK_CREATE_FILM_BAD_REQUEST.json")
    List<Map<String, Object>> bulkCreateFilmBadRequest;

    @GivenTextResource(TEST_JSON_FOLDER + "/CREATE_FILM_REQUEST_CSV.csv")
    String createFilmRequestCSV;

    @GivenTextResource(TEST_JSON_FOLDER + "/CREATE_FILM_BAD_REQUEST_CSV.csv")
    String createFilmBadRequestCSV;

    @GivenJsonResource(TEST_JSON_FOLDER + "/BULK_CREATE_DIRECTOR_REQUEST.json")
    List<Map<String, Object>> bulkCreateDirectorRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/BULK_CREATE_DIRECTOR_BAD_REQUEST.json")
    List<Map<String, Object>> bulkCreateDirectorBadRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/BULK_CREATE_REVIEW_REQUEST.json")
    List<Map<String, Object>> bulkCreateReviewRequest;

    @Test
    @DisplayName("Bulk create films with JSON type.")
    void bulkCreateFilmsWithJsonType() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkCreateFilmRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                .andDo(document(DB_NAME + "-bulk-create-films-with-actor.json-type"));
    }

    @Test
    @DisplayName("Bulk create films with CSV type.")
    void bulkCreateFilmsWithCsvType() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/bulk")
                        .contentType(new MediaType("text", "csv"))
                        .content(createFilmRequestCSV))
                .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                .andDo(document(DB_NAME + "-bulk-create-films-with-csv-type"));
    }

    @Test
    @DisplayName("Failed to bulk create films with CSV type.")
    void bulkCreateFilmsWithCsvTypeError() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/bulk")
                        .contentType(new MediaType("text", "csv"))
                        .accept(APPLICATION_JSON)
                        .content(createFilmBadRequestCSV))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document(DB_NAME + "-bulk-create-films-with-csv-type-error"));
    }

    @Test
    @DisplayName("Failed to bulk create films with JSON type.")
    void bulkCreateFilmsWithJsonTypeError() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkCreateFilmBadRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document(DB_NAME + "-bulk-create-films-with-actor.json-type-error"));
    }

    @Test
    @DisplayName("Bulk create directors.")
    void bulkCreateDirectors() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/director/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(bulkCreateDirectorRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(DB_NAME + "-bulk-create-directors"));
    }

    @Test
    @DisplayName("Bulk create directors with the wrong tsid type.")
    void bulkCreateDirectorWithWrongTsidType() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/director/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsid", "director_id")
                        .param("tsidType", "string")
                        .content(objectMapper.writeValueAsString(bulkCreateDirectorBadRequest))
                )
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document(DB_NAME + "-bulk-create-directors-with-wrong-tsid-type"));
    }

    @Test
    @DisplayName("Create reviews with default tsid type.")
    void bulkCreateReviewWithDefaultTsidType() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/review/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(bulkCreateReviewRequest))
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document(DB_NAME + "-bulk-create-reviews-with-default-tsid-type"));
    }

}
