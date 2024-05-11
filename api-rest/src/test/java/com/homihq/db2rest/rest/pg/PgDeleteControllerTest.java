package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(190)
class PgDeleteControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete a Director")
    void delete_single_record() throws Exception {
        mockMvc.perform(delete("/pgsqldb/director")
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name==\"Alex\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(1)))
                //.andDo(print())
                .andDo(document("pg-delete-a-director"));
    }

    @Test
    @DisplayName("Delete all records while allowSafeDelete=true")
    void delete_all_records_with_allow_safe_delete_true() throws Exception {
        mockMvc.perform(delete("/pgsqldb/director")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid delete operation , safe set to true")))
                //.andDo(print())
                .andDo(document("pg-delete-a-director"));
    }

    @Test
    @DisplayName("Column Does Not Exist")
    void column_does_not_exist() throws Exception {
        mockMvc.perform(delete("/pgsqldb/director")
                        .accept(APPLICATION_JSON)
                        .param("filter", "_name==\"Alex\""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail",
                        containsString("Missing column director._name")))
                //.andDo(print())
                .andDo(document("pg-delete-a-director"));
    }

    @Test
    @DisplayName("Foreign Key Constraint Violation")
    void foreign_key_constraint_violation() throws Exception {
        mockMvc.perform(delete("/pgsqldb/language")
                        .accept(APPLICATION_JSON)
                        .param("filter", "name==\"English\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("ERROR: update or delete on table \"language\" violates foreign key " +
                                "constraint \"film_language_id_fkey\" on table \"film")))
                //.andDo(print())
                .andDo(document("pg-delete-a-director"));
    }
}
