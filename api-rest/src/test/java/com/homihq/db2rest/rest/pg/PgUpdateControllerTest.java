package com.homihq.db2rest.rest.pg;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(170)
@TestWithResources
class PgUpdateControllerTest extends PostgreSQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/UPDATE_FILM_REQUEST.json")
    Map<String, Object> updateFilmRequest;

    @GivenJsonResource("/testdata/UPDATE_NON_EXISTING_FILM_REQUEST.json")
    Map<String, Object> updateNonExistingFilmRequest;

    @GivenJsonResource("/testdata/UPDATE_NON_EXISTING_TABLE.json")
    Map<String, Object> updateNonExistingTable;

    @GivenJsonResource("/testdata/UPDATE_FILMS_REQUEST.json")
    Map<String, Object> updateFilmsRequest;

    @Test
    @DisplayName("Update an existing film")
    void updateExistingFilm() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/film")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "title==\"ACADEMY DINOSAUR\"")
                        .content(objectMapper.writeValueAsString(updateFilmRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("pg-update-existing-film"));
    }

    @Test
    @DisplayName("Update a non-existing film")
    void updateNonExistingFilm() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/film")
                        .characterEncoding(UTF_8)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "title==\"BAAHUBALI\"")
                        .content(objectMapper.writeValueAsString(updateNonExistingFilmRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                //.andDo(print())
                .andDo(document("pg-update-non-existing-film"));
    }

    @Test
    @DisplayName("Update non-existing table")
    void updateNonExistingTable() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/unknown_table")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "sample_col==\"sample value 1\"")
                        .content(objectMapper.writeValueAsString(updateNonExistingTable))
                )
                .andExpect(status().isNotFound())
                //.andDo(print())
                .andDo(document("pg-update-non-existing-table"));
    }

    @Test
    @DisplayName("Updating multiple films")
    void updateMultipleColumns() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "rating==\"G\"")
                        .content(objectMapper.writeValueAsString(updateFilmsRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("pg-update-multiple-films"));
    }

}
