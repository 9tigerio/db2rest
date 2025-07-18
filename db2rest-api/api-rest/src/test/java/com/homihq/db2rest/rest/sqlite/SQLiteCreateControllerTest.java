package com.homihq.db2rest.rest.sqlite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(80)
@TestWithResources
class SQLiteCreateControllerTest extends SQLiteBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST.json")
    Map<String, Object> CREATE_FILM_REQUEST;

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST_ERROR.json")
    Map<String, Object> CREATE_FILM_REQUEST_ERROR;

    @GivenJsonResource("/testdata/CREATE_VANITY_VAN_REQUEST.json")
    Map<String, Object> CREATE_VANITY_VAN_REQUEST;

    @GivenJsonResource("/testdata/CREATE_DIRECTOR_REQUEST.json")
    Map<String, Object> CREATE_DIRECTOR_REQUEST;

    @Test
    @DisplayName("Create a new film")
    void createFilm() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/film")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_FILM_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.film_id").exists())
                .andDo(document("sqlite-create-film"));

        //Verify
        String response = mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Integer filmId = JsonPath.read(response, "$.data[?(@.title == 'ACADEMY DINOSAUR')].film_id");
        assertTrue(filmId != null);
    }

    @Test
    @DisplayName("Create a new film with subset of columns")
    void createFilmWithSubsetOfColumns() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/film")
                        .contentType(APPLICATION_JSON)
                        .param("columns", "title,description,language_id")
                        .content(objectMapper.writeValueAsString(CREATE_FILM_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.film_id").exists())
                .andDo(document("sqlite-create-film-subset"));
    }

    @Test
    @DisplayName("Create a new film - invalid payload")
    void createFilmInvalidPayload() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/film")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_FILM_REQUEST_ERROR)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("NOT NULL constraint failed")))
                .andDo(document("sqlite-create-film-error"));
    }

    @Test
    @DisplayName("Create a new vanity van with TSID")
    void createVanityVanWithTSID() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/vanity_van")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_VANITY_VAN_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.van_id").exists())
                .andDo(document("sqlite-create-vanity-van"));
    }

    @Test
    @DisplayName("Create a new director with TSID")
    void createDirectorWithTSID() throws Exception {

        mockMvc.perform(post(VERSION + "/sqlitedb/director")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CREATE_DIRECTOR_REQUEST)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.director_id").exists())
                .andDo(document("sqlite-create-director"));
    }

    @Test
    @DisplayName("Create a new actor")
    void createActor() throws Exception {

        Map<String, Object> actor = Map.of(
                "first_name", "JOHN",
                "last_name", "DOE"
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/actor")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actor)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.actor_id").exists())
                .andDo(document("sqlite-create-actor"));
    }

    @Test
    @DisplayName("Create a new employee")
    void createEmployee() throws Exception {

        Map<String, Object> employee = Map.of(
                "first_name", "Jane",
                "last_name", "Doe",
                "is_active", true
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/employee")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.emp_id").exists())
                .andDo(document("sqlite-create-employee"));
    }
}