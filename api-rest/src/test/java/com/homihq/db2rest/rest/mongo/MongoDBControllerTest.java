package com.homihq.db2rest.rest.mongo;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MongoBaseIntegrationTest;
import com.homihq.db2rest.multidb.DatabaseContextHolder;
import com.homihq.db2test.mongo.multidb.RoutingMongoTemplate;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.mongo.rest.api.MongoRestApi.VERSION;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestWithResources
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(410)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MongoDBControllerTest extends MongoBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_ACTOR_REQUEST.json")
    Map<String, Object> createActorRequest;

    @GivenJsonResource("/testdata/BULK_CREATE_ACTOR_REQUEST.json")
    List<Map<String, Object>> bulkCreateActorRequest;

    @GivenJsonResource("/testdata/UPDATE_ACTOR_REQUEST.json")
    Map<String, Object> updateActorRequest;

    @Autowired
    private RoutingMongoTemplate routingMongoTemplate;

    @Test
    @Order(1)
    @DisplayName("Create an actor")
    void create() throws Exception {
        mockMvc.perform(post(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createActorRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.timestamp").exists())
                .andDo(document("mongodb-create-an-actor"));

        DatabaseContextHolder.setCurrentDbId("mongo");

        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("KEVIN")),
                "Sakila_actors");
    }

    @Test
    @Order(2)
    @DisplayName("Create an actor with with include fields")
    void createWithIncludeFields() throws Exception {
        mockMvc.perform(post(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("fields", "FirstName, LastName")
                        .content(objectMapper.writeValueAsString(createActorRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.timestamp").exists())
                .andDo(document("mongodb-create-an-actor-with-include-fields"));

        DatabaseContextHolder.setCurrentDbId("mongo");

        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("KEVIN")),
                "Sakila_actors");
    }

    @Test
    @Order(3)
    @DisplayName("Create many actors")
    void createBulk() throws Exception {
        mockMvc.perform(post(VERSION + "/mongo/Sakila_actors/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkCreateActorRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))

                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys", hasSize(2)))
                .andExpect(jsonPath("$.keys", allOf(notNullValue())))
                .andDo(document("mongodb-bulk-create-actors"));

        DatabaseContextHolder.setCurrentDbId("mongo");

        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("VIVIEN")),
                "Sakila_actors");
        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("CUBA")),
                "Sakila_actors");
    }

    @Test
    @Order(4)
    @DisplayName("Create many actors with include fields")
    void createBulkWithIncludeFields() throws Exception {
        mockMvc.perform(post(VERSION + "/mongo/Sakila_actors/bulk")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("fields", "FirstName, LastName")
                        .content(objectMapper.writeValueAsString(bulkCreateActorRequest))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andExpect(jsonPath("$.rows", hasItem(1)))

                .andExpect(jsonPath("$.keys").isArray())
                .andExpect(jsonPath("$.keys", hasSize(2)))
                .andExpect(jsonPath("$.keys", allOf(notNullValue())))
                .andDo(document("mongodb-bulk-create-actors-with-include-fields"));

        DatabaseContextHolder.setCurrentDbId("mongo");

        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("VIVIEN")),
                "Sakila_actors");
        routingMongoTemplate.get().remove(query(Criteria
                        .where("FirstName")
                        .is("CUBA")),
                "Sakila_actors");
    }

    @Test
    @Order(5)
    @DisplayName("Update an existing Actor")
    void updateExistingActor() throws Exception {
        mockMvc.perform(patch(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==PENELOPE")
                        .content(objectMapper.writeValueAsString(updateActorRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mongodb-update-existing-actor"));
    }

    @Test
    @Order(6)
    @DisplayName("Update a non-existing Actor")
    void updateNonExistingActor() throws Exception {
        mockMvc.perform(patch(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==Michael")
                        .content(objectMapper.writeValueAsString(updateActorRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(document("mongodb-update-non-existing-actor"));
    }

    @Test
    @Order(7)
    @DisplayName("Update non-existing Collection")
    void updateNonExistingCollection() throws Exception {
        mockMvc.perform(patch(VERSION + "/mongo/unknown_collection")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==Michael")
                        .content(objectMapper.writeValueAsString(updateActorRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(document("mongodb-update-non-existing-collection"));
    }

    @Test
    @Order(8)
    @DisplayName("Update multiple Actors")
    void updateExistingActors() throws Exception {
        mockMvc.perform(patch(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "LastName==ZELLWEGER")
                        .content(objectMapper.writeValueAsString(updateActorRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("mongodb-update-existing-actors"));
    }

    @Test
    @Order(9)
    @DisplayName("Test find all actors - all fields")
    void findAllActors() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.*").isArray())
                .andDo(document("mongodb-get-all-actors-all-fields"));
    }

    @Test
    @Order(10)
    @DisplayName("Test find all actors - 2 fields")
    void findAllActorsWithTwoFields() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "FirstName,LastName")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$[0].FirstName", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[0].LastName", equalTo("GUINESS")))
                .andDo(document("mongodb-find-all-actors-2-fields"));
    }

    @Test
    @Order(11)
    @DisplayName("Test find all actors with filter")
    void findAllActorsWithFilter() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("filter", "FirstName==PENELOPE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].FirstName", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[0].LastName", equalTo("GUINESS")))
                .andDo(document("mongodb-find-all-actors-with-filter"));
    }

    @Test
    @Order(12)
    @DisplayName("Test find all actors with sorting")
    void findAllActorsWithSorting() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("sort", "FirstName;desc,LastName")
                        .param("filter", "LastName==ZELLWEGER")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].FirstName", equalTo("MINNIE")))
                .andExpect(jsonPath("$[1].FirstName", equalTo("JULIA")))
                .andDo(document("mongodb-find-all-actors-with-sorting"));
    }

    @Test
    @Order(13)
    @DisplayName("Test find all actors with pagination")
    void findAllActorsWithPagination() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "FirstName,LastName")
                        .param("offset", "2")
                        .param("limit", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].FirstName", equalTo("ED")))
                .andExpect(jsonPath("$[1].FirstName", equalTo("JENNIFER")))
                .andExpect(jsonPath("$[2].FirstName", equalTo("JOHNNY")))
                .andDo(document("mongodb-find-all-actors-with-sorting"));
    }

    @Test
    @Order(14)
    @DisplayName("Find one actor - 2 fields")
    void findOneActor() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/one")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "FirstName,LastName")
                        .param("filter", "FirstName==PENELOPE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.FirstName", equalTo("PENELOPE")))
                .andExpect(jsonPath("$.LastName", equalTo("GUINESS")))
                .andDo(document("mongodb-get-one-actor"));
    }

    @Test
    @Order(15)
    @DisplayName("Total number of Actors")
    void countAll() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/count")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(8)))
                .andDo(document("mongodb-get-actor-count"));
    }

    @Test
    @Order(16)
    @DisplayName("Number of Actors by LastName")
    void countActorsByLastName() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/count")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "LastName==ZELLWEGER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document("mongodb-get-actor-count-by-LastName"));
    }

    @Test
    @Order(17)
    @DisplayName("Actor exists By FirstName")
    void existsByLastName() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/exists")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==JENNIFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(true)))
                .andDo(document("mongodb-actor-exists-by-LastName"));
    }

    @Test
    @Order(18)
    @DisplayName("Actor exists By unknown name")
    void existsByUnknownName() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/exists")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists", equalTo(false)))
                .andDo(document("mongodb-actor-exists-by-unknown-name"));
    }

    @Test
    @Order(19)
    @DisplayName("Actor exists throws exception when 'filter' is not present")
    void existsThrowsException() throws Exception {
        mockMvc.perform(get(VERSION + "/mongo/Sakila_actors/exists")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        hasToString("Required parameter 'filter' is not present.")))
                .andDo(document("mongodb-actor-exists-filter-is-not-present"));
    }

    @Test
    @Order(20)
    @DisplayName("Delete all documents while allowSafeDelete=true")
    void delete_all_documents_with_allow_safe_delete_true() throws Exception {
        mockMvc.perform(delete(VERSION + "/mongo/Sakila_actors")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid delete operation , safe set to true")))
                .andDo(document("mongodb-delete-an-actor"));
    }

    @Test
    @Order(21)
    @DisplayName("Delete an actor")
    void delete_single_record() throws Exception {
        mockMvc.perform(delete(VERSION + "/mongo/Sakila_actors")
                        .accept(APPLICATION_JSON)
                        .param("filter", "FirstName==BETTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(1)))
                .andDo(document("mongodb-delete-an-actor"));
    }

}
