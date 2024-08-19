package com.homihq.db2rest.rest.mariadb;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import com.homihq.db2rest.rest.DateTimeUtil;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestWithResources
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MariadbDateTimeAllTest extends MariaDBBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST.json")
    Map<String,Object> CREATE_FILM_REQUEST;

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST_ERROR.json")
    Map<String,Object> CREATE_FILM_REQUEST_ERROR;

    private final String dateTime = "2024-03-15T10:30:45.000";

    @Test
    @Order(1)
    @DisplayName("Test Create a film with datetime fields")
    void createFilmWithDateTimeFields() throws Exception {
        // Prepare the request with datetime fields
        Map<String, Object> createFilmRequestWithDateTime = new HashMap<>(CREATE_FILM_REQUEST);
        createFilmRequestWithDateTime.put("last_update", "2020-03-15T14:30:45.000");

        mockMvc.perform(post(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequestWithDateTime)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andDo(document("mariadb-create-a-film-with-datetime"));
    }

    @Test
    @Order(1)
    @DisplayName("Test Create a film with error timestamp field")
    void createFilmWithErrorDateTimeField() throws Exception{
        Map<String, Object> filmRequestWithErrorDateTime = new HashMap<>(CREATE_FILM_REQUEST_ERROR);
        filmRequestWithErrorDateTime.put("last_update", "2023-15-35T14:75:90");
        // Remove non-schema country error field to test only incorrect date time format
        filmRequestWithErrorDateTime.remove("country");
        mockMvc.perform(post(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filmRequestWithErrorDateTime)))
                .andExpect(status().isBadRequest())
                .andDo(document("mariadb-create-a-film-with-error-timestamp"));
    }

    @Test
    @Order(2)
    @DisplayName("Test update a film with datetime field")
    void updateFilmWithDateTimeField() throws Exception {
        // Prepare the request with datetime fields
        Map<String, Object> updateFilmRequestWithDateTime = new HashMap<>();
        updateFilmRequestWithDateTime.put("last_update", dateTime);

        mockMvc.perform(patch(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "title==\"Dunki\"")
                        .content(objectMapper.writeValueAsString(updateFilmRequestWithDateTime))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mariadb-update-a-film-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get a film with datetime fields")
    void getFilmWithDateTimeFields() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "title==\"Dunki\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andDo(result -> assertEquals(dateTime, DateTimeUtil.transformTimeStamp(result)))
                .andDo(document("mariadb-read-a-film-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get a film filter by timestamp")
    void getFilmFilterByTimeStamp() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "last_update > \"2023-03-15T10:30:45.00Z\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("[0].title", equalTo("Dunki")))
                .andDo(result -> assertEquals(dateTime, DateTimeUtil.transformTimeStamp(result)))
                .andDo(document("mariadb-read-a-film-filter-by-timestamp"));
    }

    @Test
    @Order(4)
    @DisplayName("Test delete a film by timestamp")
    void deleteFilmByTimeStamp() throws Exception {
        mockMvc.perform(delete(VERSION + "/mariadb/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "last_update > \"2023-03-15T10:30:45.00Z\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mariadb-delete-a-film-by-timestamp"));
    }
}