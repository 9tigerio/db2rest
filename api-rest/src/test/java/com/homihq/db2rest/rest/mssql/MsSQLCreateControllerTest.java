package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

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

@Order(502)
@TestWithResources
class MsSQLCreateControllerTest extends MsSQLBaseIntegrationTest {

    @GivenJsonResource(TEST_JSON_FOLDER + "/CREATE_FILM_REQUEST.json")
    Map<String, Object> createFilmRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/CREATE_FILM_REQUEST_ERROR.json")
    Map<String, Object> createFilmRequestError;

    @GivenJsonResource(TEST_JSON_FOLDER + "/CREATE_VANITY_VAN_REQUEST.json")
    Map<String, Object> createVanityVanRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/CREATE_DIRECTOR_REQUEST.json")
    Map<String, Object> createDirectorRequest;

    @GivenJsonResource(TEST_JSON_FOLDER + "/CREATE_FILM_REQUEST_MISSING_PAYLOAD.json")
    Map<String, Object> createFilmRequestMissingPayload;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Create a film.")
    void createFilm() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.GENERATED_KEYS").exists())
                .andExpect(jsonPath("$.keys.GENERATED_KEYS", equalTo(7)))
                .andDo(document(DB_NAME + "-create-a-film"));
    }

    @Test
    @DisplayName("Failed to create a film.")
    void createFilmError() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequestError)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document(DB_NAME + "-create-film-error"));
    }

    @Test
    @DisplayName("Create an item in non existent table.")
    void createItemNonExistentTable() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/films")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andDo(document(DB_NAME + "-create-item-in-non-existing-table"));
    }

    @Test
    @DisplayName("Create a director with TSID enabled")
    void createDirectorWithTSIDEnabled() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/director")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(createDirectorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andDo(document(DB_NAME + "-create-a-director-tsid-enabled"));
    }

    @Test
    @DisplayName("Create a director with TSID explicitly OFF")
    void createDirectorWithTSIDOff() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/director")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "false")
                        .content(objectMapper.writeValueAsString(createDirectorRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document(DB_NAME + "-create-a-director-with-tsid-OFF"));
    }

    @Test
    @DisplayName("Test Create a Vanity Van - with varchar tsid type")
    void createVanityVanWithVarcharTsIdType() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/vanity_van")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(createVanityVanRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andDo(document(DB_NAME + "-create-a-vanity-van-tsid-varchar"));
    }

    @Test
    @DisplayName("Create a film with subset of columns")
    void createFilmWithSubsetOfColumns() throws Exception {
        var result = mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .queryParam("columns", "title,description,language_id")
                        .content(objectMapper.writeValueAsString(createVanityVanRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.GENERATED_KEYS").exists())
                .andExpect(jsonPath("$.keys.GENERATED_KEYS").isNumber())
                .andDo(document(DB_NAME + "-create-a-film"))
                .andReturn();

        int pk = JsonPath.read(result.getResponse().getContentAsString(), "$.keys.GENERATED_KEYS");

        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(APPLICATION_JSON)
                        .queryParam("fields", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", pk)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].release_year").doesNotExist());

        // cleanup data
        assertTrue(deleteRow(pk));
    }

    @Test
    @DisplayName("Ignore if columns parameter is blank")
    void shouldIgnoreWhenColumnsQueryParamIsEmpty() throws Exception {
        var result = mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .queryParam("columns", "")
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys").exists())
                .andDo(document(DB_NAME + "-create-a-film"))
                .andReturn();

        int pk = JsonPath.read(result.getResponse().getContentAsString(), "$.keys.GENERATED_KEYS");

        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(APPLICATION_JSON)
                        .queryParam("select", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", pk)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].release_year", equalTo("2023")));

        // cleanup data
        assertTrue(deleteRow(pk));
    }

    @Test
    @DisplayName("Column is present in columns param but not in payload")
    void columnIsPresentInColumnsQueryParamButNotInPayload() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .queryParam("columns", "title,description,language_id")
                        .content(objectMapper.writeValueAsString(createFilmRequestMissingPayload))) //description is not in payload will be set to null
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document(DB_NAME + "-create-a-film-missing-payload-attribute-error"))
                .andReturn();
    }

    @Test
    @DisplayName("Column violates not-null constraint")
    void columnViolatesNotNullConstraint() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .queryParam("columns", "title,description")
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Cannot insert the value NULL into column 'language_id'")))
                .andDo(print())
                .andDo(document(DB_NAME + "-create-a-film-not-null-constraint"));
    }

    private boolean deleteRow(int id) {
        var query = "DELETE FROM film WHERE film_id = ?";
        return jdbcTemplate.update(query, id) == 1;
    }

}
