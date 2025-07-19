package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

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
@Order(130)
class SQLiteTemplateControllerTest extends SQLiteBaseIntegrationTest {

    //@Test
    @DisplayName("Execute SQL template - select all")
    void executeSelectAllTemplate() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/select_all")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-template-select-all"));
    }

    //@Test
    @DisplayName("Execute SQL template - select by id")
    void executeSelectByIdTemplate() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/select_by_id")
                        .param("film_id", "1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-template-select-by-id"));
    }

    //@Test
    @DisplayName("Execute SQL template - conditional render with AND operation")
    void executeConditionalRenderAndOpTemplate() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_and_op")
                        .param("film_id", "1")
                        .param("rating", "PG")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$.data[0].rating", equalTo("PG")))
                .andDo(document("sqlite-template-conditional-and"));
    }

    //@Test
    @DisplayName("Execute SQL template - conditional render with title filter")
    void executeConditionalRenderWithTitleFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_and_op")
                        .param("title", "ACADEMY")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-template-conditional-title"));
    }

    //@Test
    @DisplayName("Execute SQL template - conditional render with join")
    void executeConditionalRenderJoinTemplate() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_join")
                        .param("actor_name", "PENELOPE")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-template-conditional-join"));
    }

    //@Test
    @DisplayName("Execute SQL template - conditional render with multiple filters")
    void executeConditionalRenderMultipleFilters() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_join")
                        .param("actor_name", "PENELOPE")
                        .param("film_title", "ACADEMY")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$.data[0].first_name", equalTo("PENELOPE")))
                .andDo(document("sqlite-template-multiple-filters"));
    }

    //@Test
    @DisplayName("Execute SQL template - no parameters")
    void executeTemplateWithoutParameters() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_and_op")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(5))) // Should return all films
                .andDo(document("sqlite-template-no-parameters"));
    }

    //@Test
    @DisplayName("Execute SQL template - with header parameters")
    void executeTemplateWithHeaderParameters() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/select_by_id")
                        .header("film_id", "2")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$.data[0].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("sqlite-template-header-parameters"));
    }

    //@Test
    @DisplayName("Execute non-existent template")
    void executeNonExistentTemplate() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/non_existent_template")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-template-non-existent"));
    }

    @Test
    @DisplayName("Execute SQL template with invalid syntax")
    void executeTemplateWithInvalidSyntax() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/template/conditional_render_and_op")
                        .param("film_id", "invalid_id")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-template-invalid-syntax"));
    }
}