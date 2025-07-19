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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(110)
@TestWithResources
class SQLiteCrossJoinControllerTest extends SQLiteBaseIntegrationTest {

    @GivenJsonResource("/testdata/CROSS_JOIN_TOPS.json")
    List<Map<String, Object>> CROSS_JOIN_TOPS;

    @GivenJsonResource("/testdata/CROSS_JOIN_USERS.json")
    List<Map<String, Object>> CROSS_JOIN_USERS;

    //@Test
    @DisplayName("Cross join tops with bottoms")
    void crossJoinTopsWithBottoms() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/tops")
                        .param("crossJoin", "bottoms")
                        .param("fields", "tops.top_item,tops.color as top_color,bottoms.bottom_item,bottoms.color as bottom_color")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(9))) // 3 tops x 3 bottoms
                .andExpect(jsonPath("$.data[0].top_item", equalTo("sweater")))
                .andExpect(jsonPath("$.data[0].bottom_item", equalTo("jeans")))
                .andDo(document("sqlite-cross-join-tops-bottoms"));
    }

   // @Test
    @DisplayName("Cross join users with userprofile")
    void crossJoinUsersWithUserProfile() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/users")
                        .param("crossJoin", "userprofile")
                        .param("fields", "users.username,userprofile.firstname,userprofile.lastname")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1))) // 1 user x 1 profile
                .andExpect(jsonPath("$.data[0].username", equalTo("admin")))
                .andExpect(jsonPath("$.data[0].firstname", equalTo("admin")))
                .andDo(document("sqlite-cross-join-users-userprofile"));
    }

    //@Test
    @DisplayName("Cross join with filter")
    void crossJoinWithFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/tops")
                        .param("crossJoin", "bottoms")
                        .param("fields", "tops.top_item,tops.color as top_color,bottoms.bottom_item,bottoms.color as bottom_color")
                        .param("filter", "tops.color==red")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3))) // 1 red top x 3 bottoms
                .andExpect(jsonPath("$.data[0].top_color", equalTo("red")))
                .andDo(document("sqlite-cross-join-with-filter"));
    }

    //@Test
    @DisplayName("Cross join with sorting")
    void crossJoinWithSorting() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/tops")
                        .param("crossJoin", "bottoms")
                        .param("fields", "tops.top_item,bottoms.bottom_item")
                        .param("sort", "tops.top_item,bottoms.bottom_item")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(9)))
                .andExpect(jsonPath("$.data[0].top_item", equalTo("shirt")))
                .andExpect(jsonPath("$.data[0].bottom_item", equalTo("jeans")))
                .andDo(document("sqlite-cross-join-with-sorting"));
    }

    //@Test
    @DisplayName("Cross join with pagination")
    void crossJoinWithPagination() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/tops")
                        .param("crossJoin", "bottoms")
                        .param("fields", "tops.top_item,bottoms.bottom_item")
                        .param("limit", "4")
                        .param("offset", "0")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(4)))
                .andDo(document("sqlite-cross-join-with-pagination"));
    }

    //@Test
    @DisplayName("Cross join with color matching")
    void crossJoinWithColorMatching() throws Exception {

        // Create join specification following the db2rest documentation format
        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "bottoms",
                "fields", List.of("bottom_item", "color:bottom_color")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/tops/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "top_item,color:top_color")
                        .param("filter", "tops.color==bottoms.color") // Add filter for color matching
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Only blue shirt with blue jeans
                .andExpect(jsonPath("$[0].top_color", equalTo("blue")))
                .andExpect(jsonPath("$[0].bottom_color", equalTo("blue")))
                .andDo(document("sqlite-cross-join-color-matching"));
    }

    //@Test
    @DisplayName("Cross join with size filter")
    void crossJoinWithSizeFilter() throws Exception {

        List<Map<String, Object>> joinSpec = List.of(
            Map.of(
                "table", "bottoms",
                "fields", List.of("bottom_item", "size:bottom_size")
            )
        );

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post(VERSION + "/sqlitedb/tops/_expand")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinSpec))
                        .param("fields", "top_item,size:top_size")
                        .param("filter", "size==S")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // 1 small top x 3 bottoms
                .andExpect(jsonPath("$[0].top_size", equalTo("S")))
                .andDo(document("sqlite-cross-join-size-filter"));
    }

    @Test
    @DisplayName("Cross join with non-existent table")
    void crossJoinWithNonExistentTable() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/tops")
                        .param("crossJoin", "non_existent_table")
                        .param("fields", "tops.top_item")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-cross-join-non-existent-table"));
    }
}