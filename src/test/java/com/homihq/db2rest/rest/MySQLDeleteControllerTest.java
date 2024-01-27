package com.homihq.db2rest.rest;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MySQLDeleteControllerTest extends MySQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete a Director")
    void delete_single_record() throws Exception {
        mockMvc.perform(delete("/director")
                        .param("filter", "first_name==\"Alex\"")
                       // .header("Content-Profile", "sakila")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(1)))
                //.andDo(print())
                .andDo(document("mysql-delete-a-director"));
    }

    @Test
    @DisplayName("Delete all records while allowSafeDelete=true")
    void delete_all_records_with_allow_safe_delete_true() throws Exception {
        mockMvc.perform(delete("/director")
                       // .header("Content-Profile", "sakila")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid delete operation , safe set to true")))
                //.andDo(print())
                .andDo(document("mysql-delete-a-director"));
    }

    @Test
    @DisplayName("Column Does Not Exist")
    void column_does_not_exist() throws Exception {
        mockMvc.perform(delete("/director")
                        .param("filter", "_name==\"Alex\"")
                       //.header("Content-Profile", "sakila")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid column director._name")))
                .andDo(print())
                .andDo(document("mysql-delete-a-director"));
    }

    @Test
    @DisplayName("Foreign Key Constraint Violation")
    void foreign_key_constraint_violation() throws Exception {
        mockMvc.perform(delete("/language")
                        .param("filter", "name==\"ENGLISH\"")
                        //.header("Content-Profile", "sakila")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Cannot delete or update a parent row: a foreign key constraint fails")))
                //.andDo(print())
                .andDo(document("mysql-delete-a-director"));
    }
}
