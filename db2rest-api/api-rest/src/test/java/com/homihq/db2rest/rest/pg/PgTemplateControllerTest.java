package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(102)
@DisplayName("Parameterized SQL template")
class PgTemplateControllerTest extends PostgreSQLBaseIntegrationTest {

    public final static int ID = 1;

    @Test
    @DisplayName("Test find all films with sql template")
    void findAllFilms() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/select_all")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(15)))
                .andDo(document("pgsql-template-get-all-films-all-columns"));
    }

    @Test
    @DisplayName("Find film by id with request param")
    void findFilmByID() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/select_by_id")
                        .param("film_id", String.valueOf(ID))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("pgsql-template-get-film-by-id-with-params"));
    }

    @Test
    @DisplayName("Find film by id with headers")
    void findFilmByIDWithHeader() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/select_by_id")
                        .header("film_id", String.valueOf(ID))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("pgsql-template-get-film-by-id-with-headers"));
    }

    @Test
    @DisplayName("Find film by id with custom path")
    void findFilmByIDWithPath() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/select_by_id/" + ID)
                        .header("paths", "film_id")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andDo(document("pgsql-template-get-film-by-id-with-user-path"));
    }

    @Test
    @DisplayName("Failed to find film by id without provided param")
    void findFilmByIDWithRequiredConstraint() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/select_by_id_is_required")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(document("pgsql-template-get-film-by-id-with-required-constraint"));
    }

    @Test
    @DisplayName("Conditional render: select all when no request params")
    void selectWithConditionalRenderNoRequestParams() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/conditional_render_and_op")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(4), hasSize(9), hasSize(8))))
                .andExpect(jsonPath("$[0].*", hasSize(15)))
                .andDo(document("pgsql-template-conditional-render-no-request-params"));
    }

    @Test
    @DisplayName("Conditional render: select render one condition")
    void selectWithConditionalRenderWithID() throws Exception {
        var id = 4;
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/conditional_render_and_op")
                        .param("film_id", String.valueOf(id))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(id))
                .andDo(document("pgsql-template-conditional-render-with-id-condition"));
    }

    @Test
    @DisplayName("Conditional render: render both and operations")
    void selectWithConditionalRenderWithField() throws Exception {
        var id = 4;
        var rating = "G";
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/conditional_render_and_op")
                        .param("film_id", String.valueOf(id))
                        .param("rating", rating)
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(id))
                .andExpect(jsonPath("$[0].rating").value(rating))
                .andDo(document("pgsql-template-conditional-render-render-both-and-operations"));
    }


    @Test
    @DisplayName("Conditional render join: skip render join, select film by id")
    void selectWithConditionalRenderJoinWithoutInput() throws Exception {
        var film_id = 1;
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/conditional_render_join")
                        .param("film_id", String.valueOf(film_id))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(film_id))
                .andDo(document("pgsql-template-conditional-render-join-skip-render"));
    }

    @Test
    @DisplayName("Conditional render join: execute rendered join")
    void selectWithConditionalRenderJoin() throws Exception {
        var film_id = 1;
        var language_id = 1;
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/conditional_render_join")
                        .param("film_id", String.valueOf(film_id))
                        .param("language_id", String.valueOf(language_id))
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(film_id))
                .andExpect(jsonPath("$[0].language_name").value(Matchers.matchesPattern("\\s*English\\s*")))
                .andDo(document("pgsql-template-conditional-render-join"));
    }
}
