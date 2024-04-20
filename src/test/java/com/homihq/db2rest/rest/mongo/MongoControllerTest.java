package com.homihq.db2rest.rest.mongo;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MongodbContainerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("it-mongo")
@TestWithResources
class MongoControllerTest extends MongodbContainerConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_ACTOR_REQUEST.json")
    Map<String, Object> CREATE_ACTOR_REQUEST;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .snippets().withTemplateFormat(TemplateFormats.markdown())
                )
                .build();
    }

    @Test
    @DisplayName("Create an actor")
    void create() throws Exception {

        mockMvc.perform(post("/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_ACTOR_REQUEST))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.timestamp").exists())
                .andDo(document("mongodb-create-an-actor"));

        mongoTemplate.remove(query(Criteria
                        .where("FirstName")
                        .is("KEVIN")),
                "Sakila_actors");
    }

    @Test
    @DisplayName("Test find all actors - all fields")
    void findAllActors() throws Exception {
        mockMvc.perform(get("/Sakila_actors")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(8)))
                .andExpect(jsonPath("$.*").isArray())
                .andDo(document("mongodb-get-all-actors-all-fields"));
    }

    @Test
    @DisplayName("Test find all actors - 2 fields")
    void findAllActorsWithTwoFields() throws Exception {
        mockMvc.perform(get("/Sakila_actors")
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
    @DisplayName("Test find all actors with filter")
    void findAllActorsWithFilter() throws Exception {
        mockMvc.perform(get("/Sakila_actors")
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
    @DisplayName("Test find all actors with sorting")
    void findAllActorsWithSorting() throws Exception {
        mockMvc.perform(get("/Sakila_actors")
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
    @DisplayName("Test find all actors with pagination")
    void findAllActorsWithPagination() throws Exception {
        mockMvc.perform(get("/Sakila_actors")
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
    @DisplayName("Find one actor - 2 fields")
    void findOneActor() throws Exception {
        mockMvc.perform(get("/Sakila_actors/one")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "FirstName,LastName")
                        .param("filter", "FirstName==PENELOPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.FirstName", equalTo("PENELOPE")))
                .andExpect(jsonPath("$.LastName", equalTo("GUINESS")))
                .andDo(document("mongodb-get-one-actor"));
    }

}