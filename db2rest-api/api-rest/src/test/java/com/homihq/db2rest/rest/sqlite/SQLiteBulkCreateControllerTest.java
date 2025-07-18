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

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(90)
@TestWithResources
class SQLiteBulkCreateControllerTest extends SQLiteBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/BULK_CREATE_ACTOR_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_ACTOR_REQUEST;

    @GivenJsonResource("/testdata/BULK_CREATE_DIRECTOR_REQUEST.json")
    List<Map<String, Object>> BULK_CREATE_DIRECTOR_REQUEST;

    @Test
    @DisplayName("Bulk create actors")
    void bulkCreateActors() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/actor/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_ACTOR_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", equalTo(3)))
                .andDo(document("sqlite-bulk-create-actors"));

        // Verify the bulk creation
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==BULK")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andDo(document("sqlite-verify-bulk-actors"));
    }

    @Test
    @DisplayName("Bulk create directors with TSID")
    void bulkCreateDirectorsWithTSID() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/director/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(BULK_CREATE_DIRECTOR_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("sqlite-bulk-create-directors"));

        // Verify the bulk creation
        mockMvc.perform(get(VERSION + "/sqlitedb/director")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andDo(document("sqlite-verify-bulk-directors"));
    }

    @Test
    @DisplayName("Bulk create employees")
    void bulkCreateEmployees() throws Exception {

        List<Map<String, Object>> employees = List.of(
                Map.of("first_name", "John", "last_name", "Smith", "is_active", true),
                Map.of("first_name", "Jane", "last_name", "Doe", "is_active", false),
                Map.of("first_name", "Bob", "last_name", "Johnson", "is_active", true)
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/employee/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employees)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", equalTo(3)))
                .andDo(document("sqlite-bulk-create-employees"));

        // Verify the bulk creation
        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .param("filter", "first_name==John")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("John")))
                .andDo(document("sqlite-verify-bulk-employees"));
    }

    @Test
    @DisplayName("Bulk create with subset of columns")
    void bulkCreateWithSubsetOfColumns() throws Exception {

        List<Map<String, Object>> partialActors = List.of(
                Map.of("first_name", "PARTIAL", "last_name", "ACTOR1"),
                Map.of("first_name", "PARTIAL", "last_name", "ACTOR2")
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/actor/bulk")
                        .param("columns", "first_name,last_name")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialActors)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("sqlite-bulk-create-subset"));

        // Verify the bulk creation
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==PARTIAL")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andDo(document("sqlite-verify-bulk-subset"));
    }

    @Test
    @DisplayName("Bulk create with invalid payload")
    void bulkCreateWithInvalidPayload() throws Exception {

        List<Map<String, Object>> invalidActors = List.of(
                Map.of("first_name", "INVALID"), // Missing required last_name
                Map.of("last_name", "INVALID") // Missing required first_name
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/actor/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidActors)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-bulk-create-invalid"));
    }

    @Test
    @DisplayName("Bulk create empty list")
    void bulkCreateEmptyList() throws Exception {

        List<Map<String, Object>> emptyList = List.of();

        mockMvc.perform(post(VERSION + "/sqlitedb/actor/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyList)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-bulk-create-empty"));
    }

    @Test
    @DisplayName("Bulk create to non-existent table")
    void bulkCreateToNonExistentTable() throws Exception {

        List<Map<String, Object>> data = List.of(
                Map.of("name", "Test")
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/non_existent_table/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-bulk-create-non-existent-table"));
    }
}