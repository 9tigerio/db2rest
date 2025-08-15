package com.homihq.db2rest.rest.sqlite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(40)
@TestWithResources
class SQLiteUpdateControllerTest extends SQLiteBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/UPDATE_FILM_REQUEST.json")
    Map<String, Object> UPDATE_FILM_REQUEST;

    @GivenJsonResource("/testdata/UPDATE_ACTOR_REQUEST.json")
    Map<String, Object> UPDATE_ACTOR_REQUEST;

    @GivenJsonResource("/testdata/UPDATE_EMPLOYEE_REQUEST.json")
    Map<String, Object> UPDATE_EMPLOYEE_REQUEST;

    @Test
    @DisplayName("Update a film")
    void updateFilm() throws Exception {

        mockMvc.perform(patch(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UPDATE_FILM_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-update-film"));

        // Verify the update
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-verify-film-update"));
    }

    @Test
    @DisplayName("Update an actor")
    void updateActor() throws Exception {
        mockMvc.perform(patch(VERSION + "/sqlitedb/actor")
                        .param("filter", "actor_id==1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UPDATE_ACTOR_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-update-actor"));

        // Verify the update
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "actor_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].last_name", equalTo("semmens")))
                .andDo(document("sqlite-verify-actor-update"));
    }

    @Test
    @DisplayName("Update an employee")
    void updateEmployee() throws Exception {

        mockMvc.perform(patch(VERSION + "/sqlitedb/employee")
                .param("filter", "emp_id==1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UPDATE_EMPLOYEE_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-update-employee"));

        // Verify the update
        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                .param("filter", "emp_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first_name", equalTo("Alice")))
                .andDo(document("sqlite-verify-employee-update"));
    }

    @Test
    @DisplayName("Update with subset of columns")
    void updateWithSubsetOfColumns() throws Exception {

        Map<String, Object> partialUpdate = Map.of(
                "title", "PARTIAL UPDATE TEST"
        );

        mockMvc.perform(patch(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==2")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-update-partial"));

        // Verify the update
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                .param("filter", "film_id==2")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("PARTIAL UPDATE TEST")))
                .andExpect(jsonPath("$[0].language_id", equalTo(1))) // Should remain unchanged
                .andDo(document("sqlite-verify-partial-update"));
    }

    @Test
    @DisplayName("Update with filter")
    void updateWithFilter() throws Exception {

        Map<String, Object> bulkUpdate = Map.of(
                "rating", "PG-13"
        );

        mockMvc.perform(patch(VERSION + "/sqlitedb/film")
                        .param("filter", "language_id==1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(5)))
                .andDo(document("sqlite-update-with-filter"));

        // Verify the bulk update
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "language_id==1")
                        .param("fields", "film_id,title,rating")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rating", equalTo("PG-13")))
                .andDo(document("sqlite-verify-bulk-update"));
    }

    @Test
    @DisplayName("Update non-existent record")
    void updateNonExistentRecord() throws Exception {

        mockMvc.perform(patch(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==999")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UPDATE_FILM_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(document("sqlite-update-non-existent"));
    }

    @Test
    @DisplayName("Update with invalid payload")
    void updateWithInvalidPayload() throws Exception {

        Map<String, Object> invalidUpdate = Map.of(
                "language_id", "invalid_value"
        );

        mockMvc.perform(patch(VERSION + "/sqlitedb/film/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-update-invalid-payload"));
    }
}