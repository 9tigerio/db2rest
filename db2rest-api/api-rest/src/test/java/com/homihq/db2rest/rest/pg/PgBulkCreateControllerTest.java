package com.homihq.db2rest.rest.pg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.GivenTextResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(181)
@TestWithResources
class PgBulkCreateControllerTest extends PostgreSQLBaseIntegrationTest {

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
        mockMvc.perform(post(VERSION + "/pgsqldb/film/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_FILM_REQUEST))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))

                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys", hasSize(2)))
                .andExpect(jsonPath("$.keys", allOf(notNullValue())))
                // .andDo(print())
                .andDo(document("pg-bulk-create-films"));
    }

    @Test
    @DisplayName("Create many films with CSV type.")
    void createCSV() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/film/bulk")
                        .contentType("text/csv")
                        .accept(APPLICATION_JSON)
                        .content(CREATE_FILM_REQUEST_CSV))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))
                .andExpect(jsonPath("$.keys", hasSize(2)))
                .andExpect(jsonPath("$.keys", allOf(notNullValue())))
                //.andDo(print())
                .andDo(document("pg-bulk-create-films-csv"));
    }

    @Test
    @DisplayName("Create many films with CSV type resulting error.")
    void createCSVWithError() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/film/bulk")
                        .contentType("text/csv")
                        .accept(APPLICATION_JSON)
                        .content(CREATE_FILM_BAD_REQUEST_CSV))
                .andExpect(status().isNotFound())
                //.andDo(print())
                .andDo(document("pg-bulk-create-films-csv-error"));

    }

    @Test
    @DisplayName("Create many films with failure.")
    void createError() throws Exception {

        mockMvc.perform(post(VERSION + "/pgsqldb/film/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_FILM_BAD_REQUEST))
                )
                .andExpect(status().isNotFound())
                // .andDo(print())
                .andDo(document("pg-bulk-create-films-error"));
    }

    @Test
    @DisplayName("Create many directors.")
    void createDirector() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/director/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_DIRECTOR_REQUEST))
                )
                .andExpect(status().isCreated())
                //.andDo(print())
                .andDo(document("pg-bulk-create-directors"));
    }

    @Test
    @DisplayName("Create many directors with Int tsid type.")
    void createDirectorWithIntTsIdType() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/director/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_DIRECTOR_BAD_REQUEST))
                )
                .andExpect(status().isCreated())
                //.andDo(print())
                .andDo(document("pg-bulk-create-directors-with-int-tsid-type"));
    }

    @Test
    @DisplayName("Create reviews with STRING TSID Type.")
    void createReviewWithDefaultTsIdType() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/review/bulk")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(BULK_CREATE_REVIEW_REQUEST))
                )
                .andExpect(status().isCreated())
                //.andDo(print())
                .andDo(document("pg-bulk-create-reviews-with-string-tsid-type"));
    }

}
