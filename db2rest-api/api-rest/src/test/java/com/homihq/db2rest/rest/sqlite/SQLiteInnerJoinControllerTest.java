package com.homihq.db2rest.rest.sqlite;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_actor",
                "fields", List.of("film_id", "actor_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "actor",
                "fields", List.of("first_name", "last_name"),
                "joinType", "INNER",
                "on", List.of("actor_id==actor_id")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].first_name").exists())
                .andDo(document("sqlite-inner-join-film-actor"));
    }

    @Test
    @DisplayName("Inner join film with category")
    void innerJoinFilmWithCategory() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_category",
                "fields", List.of("film_id", "category_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "category",
                "fields", List.of("name"),
                "joinType", "INNER",
                "on", List.of("category_id==category_id")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andDo(document("sqlite-inner-join-film-category"));
    }

    @Test
    @DisplayName("Inner join with filter")
    void innerJoinWithFilter() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_actor",
                "fields", List.of("film_id", "actor_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "actor",
                "fields", List.of("first_name", "last_name"),
                "joinType", "INNER",
                "on", List.of("actor_id==actor_id"),
                "filter", "first_name==PENELOPE"
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-inner-join-with-filter"));
    }

    @Test
    @DisplayName("Inner join with sorting")
    void innerJoinWithSorting() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_actor",
                "fields", List.of("film_id", "actor_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "actor",
                "fields", List.of("first_name", "last_name"),
                "joinType", "INNER",
                "on", List.of("actor_id==actor_id"),
                "sort", "first_name"
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(6)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-inner-join-with-sorting"));
    }

    @Test
    @DisplayName("Inner join with pagination")
    void innerJoinWithPagination() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_actor",
                "fields", List.of("film_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "actor",
                "fields", List.of("actor_id", "first_name", "last_name"),
                "joinType", "INNER",
                "on", List.of("actor_id==actor_id")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("limit", "3")
                        .param("offset", "0")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-inner-join-with-pagination"));
    }

    @Test
    @DisplayName("Inner join with language")
    void innerJoinWithLanguage() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "language",
                "fields", List.of("name"),
                "joinType", "INNER",
                "on", List.of("language_id==language_id")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].name").exists())
                .andDo(document("sqlite-inner-join-with-language"));
    }

    @Test
    @DisplayName("Inner join with multiple tables")
    void innerJoinWithMultipleTables() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "film_actor",
                "fields", List.of("film_id", "actor_id"),
                "joinType", "INNER",
                "on", List.of("film_id==film_id")
            ),
            Map.of(
                "table", "actor",
                "fields", List.of("first_name", "last_name"),
                "joinType", "INNER",
                "on", List.of("actor_id==actor_id")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "title")
                        .param("limit", "5")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[0].first_name").exists())
                .andDo(document("sqlite-inner-join-multiple-tables"));
    }

    @Test
    @DisplayName("Inner join with non-existent table")
    void innerJoinWithNonExistentTable() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "non_existent_table",
                "fields", List.of("non_existent_field"),
                "joinType", "INNER",
                "on", List.of("non_existent_field==non_existent_field")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-inner-join-non-existent-table"));
    }
}