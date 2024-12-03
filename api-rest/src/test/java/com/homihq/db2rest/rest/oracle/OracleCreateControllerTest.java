package com.homihq.db2rest.rest.oracle;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.OracleBaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
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
@Order(280)
@TestWithResources
class OracleCreateControllerTest extends OracleBaseIntegrationTest {
    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST.json")
    Map<String, Object> createFilmRequest;

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST_ERROR.json")
    Map<String, Object> createFilmRequestError;

    @GivenJsonResource("/testdata/CREATE_VANITY_VAN_REQUEST.json")
    Map<String, Object> createVanityVanRequest;

    @GivenJsonResource("/testdata/CREATE_DIRECTOR_REQUEST.json")
    Map<String, Object> createDirectorRequest;

    @GivenJsonResource("/testdata/CREATE_FILM_REQUEST_MISSING_PAYLOAD.json")
    Map<String, Object> createFilmRequestMissingPayload;

    @Test
    @DisplayName("Create a film.")
    void create() throws Exception {

        mockMvc.perform(post(VERSION + "/oradb/FILM")
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequest))
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                //.andExpect(jsonPath("$.keys.GENERATED_KEY").exists())
                //.andExpect(jsonPath("$.keys.GENERATED_KEY", equalTo(5)))
                .andDo(document("oracle-create-a-film"));

    }

    @Test
    @DisplayName("Test Create a film with error.")
    void createError() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILM")
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequestError)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("oracle-create-a-film-error"));

    }

    @Test
    @DisplayName("Test create a film - non existent table.")
    void createNonExistentTable() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILMS")
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andExpect(status().isNotFound())
                //.andDo(print())
                .andDo(document("oracle-create-a-film-no-table"));

    }

    @Test
    @DisplayName("Test Create a director - TSID enabled")
    void createDirectorWithTSIDEnabled() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/DIRECTOR")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(createDirectorRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                //.andExpect(jsonPath("$.keys.director_id").exists())
                // .andExpect(jsonPath("$.keys.director_id").isNumber())
                .andDo(document("oracle-create-a-director-tsid-enabled"));

    }

    @Test
    @DisplayName("Create a director - with TSID explicitly OFF")
    void createDirectorWithTSIDOff() throws Exception {

        mockMvc.perform(post(VERSION + "/oradb/DIRECTOR")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "false")
                        .content(objectMapper.writeValueAsString(createDirectorRequest)))
                .andExpect(status().isBadRequest())
                //.andDo(print())
                .andDo(document("oracle-create-a-director-with-tsid-OFF"));

    }

    @Test
    @DisplayName("Test Create a Vanity Van - with varchar tsid type")
    void createVanityVanWithVarcharTsIdType() throws Exception {
        //TODO - MySQL return keys not working
        mockMvc.perform(post(VERSION + "/oradb/VANITY_VAN")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("tsIdEnabled", "true")
                        .content(objectMapper.writeValueAsString(createVanityVanRequest)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                //.andExpect(jsonPath("$.keys.vanity_van_id").exists())
                //.andExpect(jsonPath("$.keys.vanity_van_id").isString())
                .andDo(document("oracle-create-a-vanity-van-tsid-varchar"));
    }

    @Test
    @DisplayName("Create a film with subset of columns")
    void createFilmWithSubsetOfColumns() throws Exception {
        var result = mockMvc.perform(post(VERSION + "/oradb/FILM")
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .queryParam("columns", "title,description,language_id")
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys").exists())
                //.andExpect(jsonPath("$.keys.GENERATED_KEY",  equalTo(5)))
                .andExpect(jsonPath("$.keys.FILM_ID").isNumber())
                .andDo(document("oracle-create-a-film-col-subset"))
                .andReturn();

        var pk = JsonPath.read(result.getResponse().getContentAsString(), "$.keys.FILM_ID");

        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .queryParam("fields", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", pk)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].TITLE", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].RELEASE_YEAR").doesNotExist());

        // cleanup data
        assertTrue(deleteRow("film", "film_id", (int) pk));
    }

    @Test
    @DisplayName("Ignore if columns parameter is blank")
    void shouldIgnoreWhenColumnsQueryParamIsEmpty() throws Exception {

        var result = mockMvc.perform(post(VERSION + "/oradb/FILM")
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .queryParam("columns", "")
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys").exists())

                .andDo(document("oracle-create-a-film-columns-not-specified"))
                .andReturn();

        var pk = JsonPath.read(result.getResponse().getContentAsString(), "$.keys.FILM_ID");

        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .queryParam("fields", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", pk)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].TITLE", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].RELEASE_YEAR", equalTo("2023")))
                .andDo(print())
        ;

        // cleanup data
        assertTrue(deleteRow("film", "film_id", (int) pk));
    }

    @Test
    @DisplayName("Column is present in columns param but not in payload")
    void columnIsPresentInColumnsQueryParamButNotInPayload() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILM")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .queryParam("columns", "title,description,language_id")
                .content(objectMapper.writeValueAsString(createFilmRequestMissingPayload))) //description is not in payload will be set to null
        .andExpect(status().isBadRequest())
        //.andDo(print())
        .andDo(document("oracle-create-a-film-missing-payload-attribute-error"));
    }

    @Test
    @DisplayName("Column violates not-null constraint")
    void column_violates_not_null_constraint() throws Exception {
        mockMvc.perform(post(VERSION + "/oradb/FILM")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .queryParam("columns", "title,description")
                        .content(objectMapper.writeValueAsString(createFilmRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("ORA-01400: cannot insert NULL")))
                .andDo(print())
                .andDo(document("oracle-create-a-film-not-null-constraint"));
    }
}
