package com.homihq.db2rest.rest.mongo;

import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MongoBaseIntegrationTest;
import org.bson.Document;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static com.homihq.db2rest.mongo.rest.api.MongoRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(492)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoDBDateTimeAllTest extends MongoBaseIntegrationTest {

    private final String dateTime = "2024-03-15T10:30:45Z";
    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @Order(1)
    @DisplayName("Test Create an actor with datetime fields in MongoDB")
    void createActorWithDateTimeFields() throws Exception {
        // Prepare the request with datetime fields
        Document actorRequestWithDateTime = new Document();
        actorRequestWithDateTime.put("first_name", "Collective");
        actorRequestWithDateTime.put("last_name", "Unconscious");
        actorRequestWithDateTime.put("last_update", LocalDateTime.of(2020, 03, 15,
                14, 30, 45));

        mockMvc.perform(post(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorRequestWithDateTime)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.timestamp").isNumber())
                .andExpect(jsonPath("$.keys.date").isString())
                .andDo(document("mongodb-create-an-actor-with-datetime"));
    }

    @Test
    @Order(2)
    @DisplayName("Test update an actor with datetime field in MongoDB")
    void updateActorWithDateTimeField() throws Exception {
        // Prepare the request with datetime fields
        Document updateActorRequestWithDateTime = new Document();

        // Convert the string to a proper OffsetDateTime object
        OffsetDateTime lastUpdateDateTime = OffsetDateTime.parse("2024-03-15T10:30:45.000+00:00");
        updateActorRequestWithDateTime.put("last_update", lastUpdateDateTime.toString());

        mockMvc.perform(patch(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "last_name == Unconscious")
                        .content(objectMapper.writeValueAsString(updateActorRequestWithDateTime)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mongodb-update-an-actor-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get an actor with datetime fields in MongoDB")
    void getActorWithDateTimeFields() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/one")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "first_name == Collective"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.last_name", equalTo("Unconscious")))
                .andExpect(jsonPath("$.last_update", equalTo(dateTime)))
                .andDo(document("mongodb-get-an-actor-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get an actor filter by timestamp in MongoDB")
    void getActorFilterByTimeStamp() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/one")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "last_update > \"2023-03-15T10:30:45.00Z\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.last_name", equalTo("Unconscious")))
                .andExpect(jsonPath("$.last_update", equalTo(dateTime)))
                .andDo(document("mongodb-get-an-actor-filter-by-timestamp"));
    }

    @Test
    @Order(4)
    @DisplayName("Test delete an actor by timestamp in MongoDB")
    void deleteActorByTimeStamp() throws Exception {
        mockMvc.perform(delete(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "last_update > \"2023-03-15T05:30:45.00Z\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mongodb-delete-an-actor-by-timestamp"));
    }
}
