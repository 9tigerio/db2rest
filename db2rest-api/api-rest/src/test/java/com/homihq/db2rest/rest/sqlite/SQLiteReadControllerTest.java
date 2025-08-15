package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(10)
class SQLiteReadControllerTest extends SQLiteBaseIntegrationTest {

    @Test
    @DisplayName("Get all films")
    void getAllFilms() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-get-all-films"));
    }

    //@Test
    @DisplayName("Get film by ID")
    void getFilmById() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.film_id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$.language_id", equalTo(1)))
                .andDo(document("sqlite-get-film-by-id"));
    }

    @Test
    @DisplayName("Get films with field selection")
    void getFilmsWithFieldSelection() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("fields", "film_id,title,language_id")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[0].language_id", equalTo(1)))
                .andExpect(jsonPath("$[0].description").doesNotExist())
                .andDo(document("sqlite-get-films-field-selection"));
    }

    @Test
    @DisplayName("Get films with pagination")
    void getFilmsWithPagination() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("limit", "2")
                        .param("offset", "0")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[1].film_id", equalTo(2)))
                .andDo(document("sqlite-get-films-pagination"));
    }

    @Test
    @DisplayName("Get films with sorting")
    void getFilmsWithSorting() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("sort", "title")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-get-films-sorting"));
    }

    @Test
    @DisplayName("Get films with filter")
    void getFilmsWithFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "language_id==1")
                        .param("fields", "film_id,title,language_id")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].language_id", equalTo(1)))
                .andDo(document("sqlite-get-films-filter"));
    }

    @Test
    @DisplayName("Get all actors")
    void getAllActors() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].actor_id", equalTo(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-get-all-actors"));
    }

    //@Test
    @DisplayName("Get actor by ID")
    void getActorById() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor/1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actor_id", equalTo(1)))
                .andExpect(jsonPath("$.first_name", equalTo("PENELOPE")))
                .andExpect(jsonPath("$.last_name", equalTo("GUINESS")))
                .andDo(document("sqlite-get-actor-by-id"));
    }

    @Test
    @DisplayName("Get all employees")
    void getAllEmployees() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].emp_id", equalTo(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("Alice")))
                .andDo(document("sqlite-get-all-employees"));
    }

    @Test
    @DisplayName("Get employees with boolean filter")
    void getEmployeesWithBooleanFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .param("filter", "is_active==true")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].is_active", equalTo(1)))
                .andDo(document("sqlite-get-employees-boolean-filter"));
    }

    @Test
    @DisplayName("Get non-existent table")
    void getNonExistentTable() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/non_existent_table")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError()) //TODO review
                .andDo(document("sqlite-get-non-existent-table"));
    }
}