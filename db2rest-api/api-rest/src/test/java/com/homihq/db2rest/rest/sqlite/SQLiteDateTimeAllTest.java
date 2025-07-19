package com.homihq.db2rest.rest.sqlite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(120)
class SQLiteDateTimeAllTest extends SQLiteBaseIntegrationTest {

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @DisplayName("Create actor with timestamp")
    void createActorWithTimestamp() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Map<String, Object> actor = Map.of(
                "first_name", "TIMESTAMP",
                "last_name", "ACTOR",
                "last_update", timestamp
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/actor")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actor)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.actor_id").exists())
                .andDo(document("sqlite-create-actor-with-timestamp"));
    }

    @Test
    @DisplayName("Read actor with timestamp")
    void readActorWithTimestamp() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==TIMESTAMP")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("TIMESTAMP")))
                .andExpect(jsonPath("$[0].last_update").exists())
                .andDo(document("sqlite-read-actor-with-timestamp"));
    }

    @Test
    @DisplayName("Update actor with timestamp")
    void updateActorWithTimestamp() throws Exception {

        LocalDateTime updateTime = LocalDateTime.now();
        String timestamp = updateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Map<String, Object> update = Map.of(
                "first_name", "UPDATED_TIMESTAMP",
                "last_update", timestamp
        );

        mockMvc.perform(patch(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==TIMESTAMP")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-update-actor-with-timestamp"));

        // Verify the update
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==UPDATED_TIMESTAMP")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("UPDATED_TIMESTAMP")))
                .andDo(document("sqlite-verify-timestamp-update"));
    }

    @Test
    @DisplayName("Create employee with create_date")
    void createEmployeeWithCreateDate() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Map<String, Object> employee = Map.of(
                "first_name", "DateTime",
                "last_name", "Employee",
                "create_date", timestamp,
                "is_active", true
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/employee")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.emp_id").exists())
                .andDo(document("sqlite-create-employee-with-datetime"));
    }

    @Test
    @DisplayName("Filter by datetime")
    void filterByDatetime() throws Exception {

        // Create an employee with a specific datetime
        LocalDateTime specificTime = LocalDateTime.of(2023, 1, 15, 10, 30, 0);
        String timestamp = specificTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Map<String, Object> employee = Map.of(
                "first_name", "Filter",
                "last_name", "Test",
                "create_date", timestamp,
                "is_active", true
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/employee")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("sqlite-create-employee-for-filter"));

        // Filter by the specific datetime
        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .param("filter", "create_date==2023-01-15T10:30:00")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$", hasSize(1)))
                //.andExpect(jsonPath("$[0].first_name", equalTo("Filter")))
                .andDo(document("sqlite-filter-by-datetime"));
    }

    @Test
    @DisplayName("Sort by datetime")
    void sortByDatetime() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .param("sort", "create_date")
                        .param("fields", "emp_id,first_name,create_date")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sqlite-sort-by-datetime"));
    }

    //@Test
    @DisplayName("Create film with release_year")
    void createFilmWithReleaseYear() throws Exception {

        Map<String, Object> film = Map.of(
                "title", "SQLite Year Test",
                "description", "A test film for year handling",
                "release_year", 2023,
                "language_id", 1,
                "rental_duration", 3,
                "rental_rate", 4.99,
                "replacement_cost", 19.99,
                "rating", "PG"
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/film")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.film_id").exists())
                .andDo(document("sqlite-create-film-with-year"));

        // Verify the film with release_year
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "title==SQLite Year Test")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].release_year", equalTo(2023)))
                .andDo(document("sqlite-verify-film-year"));
    }

    @Test
    @DisplayName("Filter by year range")
    void filterByYearRange() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "release_year>=2006")
                        .param("fields", "film_id,title,release_year")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andDo(document("sqlite-filter-by-year-range"));
    }

    @Test
    @DisplayName("Bulk create with timestamps")
    void bulkCreateWithTimestamps() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        String timestamp1 = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String timestamp2 = now.plusMinutes(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        var actors = List.of(
                Map.of("first_name", "BULK1", "last_name", "TIMESTAMP", "last_update", timestamp1),
                Map.of("first_name", "BULK2", "last_name", "TIMESTAMP", "last_update", timestamp2)
        );

        mockMvc.perform(post(VERSION + "/sqlitedb/actor/bulk")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actors)))
                .andDo(print())
                .andExpect(status().isCreated())
               // .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("sqlite-bulk-create-with-timestamps"));

        // Verify the bulk creation with timestamps
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "last_name==TIMESTAMP")
                        .param("sort", "last_update")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("sqlite-verify-bulk-timestamps"));
    }
}