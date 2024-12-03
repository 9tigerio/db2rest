package com.homihq.db2rest.rest.pg;

import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(101)
@TestWithResources
class PgReadControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Test find all films - all columns.")
    void findAllFilms() throws Exception {

        mockMvc.perform(get(VERSION + "/pgsqldb/film")
                        .accept(APPLICATION_JSON).accept(APPLICATION_JSON))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(15)))
                .andDo(document("pg-get-all-films-all-columns"));
    }


    @Test
    @DisplayName("Test find all films - 3 columns")
    void findAllFilmsWithThreeCols() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/film")
                        .contentType(APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(8), hasSize(9))))
                .andExpect(jsonPath("$[0].*", hasSize(3)))
                .andDo(document("pg-find-all-films-3-columns"));
    }

    @Test
    @DisplayName("Test find all films - with column alias")
    void findAllFilmsWithColumnAlias() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/film")
                        .contentType(APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year:releaseYear")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(8))))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].releaseYear", notNullValue()))
                .andDo(document("pg-find-all-films-with-column-alias"));
    }

    @Test
    @DisplayName("Test find one record")
    void findOneFilm() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/film/one")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title")
                        .param("filter", "film_id==1")
                )
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("pg-get-one-film"));
    }
}
