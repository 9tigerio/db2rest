package com.homihq.db2rest.rest.mariadb;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.GivenTextResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(381)
@TestWithResources
class MariaDBBulkCreateControllerTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/BULK_CREATE_FILM_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_FILM_REQUEST;

    @GivenJsonResource("/testdata/BULK_CREATE_FILM_BAD_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_FILM_BAD_REQUEST;

    @GivenTextResource("/testdata/CREATE_FILM_REQUEST_CSV.csv")
    String CREATE_FILM_REQUEST_CSV;

    @GivenTextResource("/testdata/CREATE_FILM_BAD_REQUEST_CSV.csv")
    String CREATE_FILM_BAD_REQUEST_CSV;

    @GivenJsonResource("/testdata/BULK_CREATE_DIRECTOR_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_DIRECTOR_REQUEST;
    @GivenJsonResource("/testdata/BULK_CREATE_DIRECTOR_BAD_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_DIRECTOR_BAD_REQUEST;

    @GivenJsonResource("/testdata/BULK_CREATE_REVIEW_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_REVIEW_REQUEST;

    @Test
    @DisplayName("Create many films.")
    void create() throws Exception {

        mockMvc.perform(post(VERSION + "/mariadb/film/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_FILM_REQUEST))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                //.andExpect(jsonPath("$.generated_keys").isArray()) //TODO - push TSID columns
                //.andExpect(jsonPath("$.generated_keys", hasSize(2)))
                //.andExpect(jsonPath("$.generated_keys", allOf(notNullValue())))
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-films"));
    }

    @Test
    @DisplayName("Create many films with CSV type.")
    void createCSV() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/film/bulk")
                        .contentType("text/csv").accept(APPLICATION_JSON)
                        .content(CREATE_FILM_REQUEST_CSV))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                //.andExpect(jsonPath("$.generated_keys", hasSize(2)))
                //.andExpect(jsonPath("$.generated_keys", allOf(notNullValue())))
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-films-csv"));
    }

    @Test
    @DisplayName("Create many films with CSV type resulting error.")
    void createCSVWithError() throws Exception {

        mockMvc.perform(post(VERSION + "/mariadb/film/bulk")
                        .contentType("text/csv")
                        .accept(APPLICATION_JSON)
                        .content(CREATE_FILM_BAD_REQUEST_CSV))
                .andExpect(status().isBadRequest())
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-films-csv-error"));
    }

    @Test
    @DisplayName("Create many films with failure.")
    void createError() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/film/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_FILM_BAD_REQUEST))
                )
                .andExpect(status().isBadRequest())
                // .andDo(print())
                .andDo(document("mariadb-bulk-create-films-error"));
    }

    @Test
    @DisplayName("Create many directors.")
    void createDirector() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/director/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_DIRECTOR_REQUEST))
                )
                .andExpect(status().isCreated())
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-directors"));
    }

    @Test
    @DisplayName("Create many directors with wrong tsid type.")
    void createDirectorWithWrongTsidType() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/director/bulk")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsid", "director_id")
                        .param("tsidType", "string")
                        .header("Content-Profile", "sakila")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_DIRECTOR_BAD_REQUEST))
                )
                .andExpect(status().isBadRequest())
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-directors-with-wrong-tsid-type"));
    }

    @Test
    @DisplayName("Create reviews with default tsid type.")
    void createReviewWithDefaultTsidType() throws Exception {
        mockMvc.perform(post(VERSION + "/mariadb/review/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_REVIEW_REQUEST))
                )
                .andExpect(status().isCreated())
                //.andDo(print())
                .andDo(document("mariadb-bulk-create-reviews-with-default-tsid-type"));
    }

}
