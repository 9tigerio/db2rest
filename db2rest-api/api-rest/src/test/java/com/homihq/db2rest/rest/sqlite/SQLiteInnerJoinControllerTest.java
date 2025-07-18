package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.GivenJsonResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(100)
@TestWithResources
class SQLiteInnerJoinControllerTest extends SQLiteBaseIntegrationTest {

    @GivenJsonResource("/testdata/INNER_JOIN.json")
    List<Map<String, Object>> INNER_JOIN;

    @Test
    @DisplayName("Inner join film with actor")
    void innerJoinFilmWithActor() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_actor")
                        .param("join", "actor")
                        .param("fields", "film.title,actor.first_name,actor.last_name")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(6)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-inner-join-film-actor"));
    }

    @Test
    @DisplayName("Inner join film with category")
    void innerJoinFilmWithCategory() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_category")
                        .param("join", "category")
                        .param("fields", "film.title,category.name")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$.data[0].name", equalTo("Documentary")))
                .andDo(document("sqlite-inner-join-film-category"));
    }

    @Test
    @DisplayName("Inner join with filter")
    void innerJoinWithFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_actor")
                        .param("join", "actor")
                        .param("fields", "film.title,actor.first_name,actor.last_name")
                        .param("filter", "actor.first_name==PENELOPE")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-inner-join-with-filter"));
    }

    @Test
    @DisplayName("Inner join with sorting")
    void innerJoinWithSorting() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_actor")
                        .param("join", "actor")
                        .param("fields", "film.title,actor.first_name,actor.last_name")
                        .param("sort", "actor.first_name")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(6)))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("ED")))
                .andDo(document("sqlite-inner-join-with-sorting"));
    }

    @Test
    @DisplayName("Inner join with pagination")
    void innerJoinWithPagination() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_actor")
                        .param("join", "actor")
                        .param("fields", "film.title,actor.first_name,actor.last_name")
                        .param("limit", "3")
                        .param("offset", "0")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andDo(document("sqlite-inner-join-with-pagination"));
    }

    @Test
    @DisplayName("Inner join with language")
    void innerJoinWithLanguage() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "language")
                        .param("fields", "film.title,language.name")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$.data[0].name", equalTo("English")))
                .andDo(document("sqlite-inner-join-with-language"));
    }

    @Test
    @DisplayName("Inner join with multiple tables")
    void innerJoinWithMultipleTables() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "film_actor")
                        .param("join", "actor")
                        .param("join", "film_category")
                        .param("join", "category")
                        .param("fields", "film.title,actor.first_name,category.name")
                        .param("limit", "5")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-inner-join-multiple-tables"));
    }

    @Test
    @DisplayName("Inner join with non-existent table")
    void innerJoinWithNonExistentTable() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("join", "non_existent_table")
                        .param("fields", "film.title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-inner-join-non-existent-table"));
    }
}