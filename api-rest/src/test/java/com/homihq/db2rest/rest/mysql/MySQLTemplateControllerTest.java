package com.homihq.db2rest.rest.mysql;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(9)
@DisplayName("Parameterized SQL template")
class MySQLTemplateControllerTest extends MySQLBaseIntegrationTest {

    public static final int ID = 1;

    @Test
    @DisplayName("Test find all films with sql template")
    void findAllFilms() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/select_all")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(9), hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(14)))
                .andDo(document("mysql-template-get-all-films-all-columns"));
    }

    @Test
    @DisplayName("Find film by id with request param")
    void findFilmByID() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/select_by_id")
                        .param("film_id", String.valueOf(ID))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("mysql-template-get-film-by-id-with-params"));
    }

    @Test
    @DisplayName("Find film by id with headers")
    void findFilmByIDWithHeader() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/select_by_id")
                        .header("film_id", String.valueOf(ID))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("mysql-template-get-film-by-id-with-headers"));
    }

    @Test
    @DisplayName("Find film by id with custom path")
    void findFilmByIDWithPath() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/select_by_id/" + ID)
                        .header("paths", "film_id")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("mysql-template-get-film-by-id-with-user-path"));
    }

    @Test
    @DisplayName("Failed to find film by id without provided param")
    void findFilmByIDWithRequiredConstraint() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/select_by_id_is_required")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(document("mysql-template-get-film-by-id-with-required-constraint"));
    }

    @Test
    @DisplayName("Conditional render: select all when no request params")
    void selectWithConditionalRenderNoRequestParams() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/sql/conditional_render_and_op")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(9), hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(14)))
                .andDo(document("mysql-template-conditional-render-no-request-params"));
    }

    @Test
    @DisplayName("Conditional render: select render one condition")
    void selectWithConditionalRenderWithID() throws Exception {
        var id = 4;
        mockMvc.perform(get(VERSION + "/mysqldb/sql/conditional_render_and_op")
                        .param("film_id", String.valueOf(id))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(id))
                .andDo(document("mysql-template-conditional-render-with-id-condition"));
    }

    @Test
    @DisplayName("Conditional render: render both and operations")
    void selectWithConditionalRenderWithField() throws Exception {
        var id = 4;
        var rating = "G";
        mockMvc.perform(get(VERSION + "/mysqldb/sql/conditional_render_and_op")
                        .param("film_id", String.valueOf(id))
                        .param("rating", rating)
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(id))
                .andExpect(jsonPath("$[0].rating").value(rating))
                .andDo(document("mysql-template-conditional-render-render-both-and-operations"));
    }


    @Test
    @DisplayName("Conditional render join: skip render join, select film by id")
    void selectWithConditionalRenderJoinWithoutInput() throws Exception {
        var filmId = 1;
        mockMvc.perform(get(VERSION + "/mysqldb/sql/conditional_render_join")
                        .param("film_id", String.valueOf(filmId))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(filmId))
                .andDo(document("mysql-template-conditional-render-join-skip-render"));
    }

    @Test
    @DisplayName("Conditional render join: execute rendered join")
    void selectWithConditionalRenderJoin() throws Exception {
        var filmId = 1;
        var languageId = 1;
        mockMvc.perform(get(VERSION + "/mysqldb/sql/conditional_render_join")
                        .param("film_id", String.valueOf(filmId))
                        .param("language_id", String.valueOf(languageId))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(filmId))
                .andExpect(jsonPath("$[0].language_name").value("English"))
                .andDo(document("mysql-template-conditional-render-join"));
    }
}
