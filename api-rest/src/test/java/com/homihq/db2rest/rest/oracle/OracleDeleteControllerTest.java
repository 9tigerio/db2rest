package com.homihq.db2rest.rest.oracle;


import com.homihq.db2rest.OracleBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(290)
class OracleDeleteControllerTest extends OracleBaseIntegrationTest {

    @Test
    @DisplayName("Delete a Director")
    void delete_single_record() throws Exception {
        mockMvc.perform(delete(VERSION + "/oradb/DIRECTOR")
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name==\"Alex\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("oracle-delete-a-director"));
    }

    @Test
    @DisplayName("Delete all records while allowSafeDelete=true")
    void delete_all_records_with_allow_safe_delete_true() throws Exception {
        mockMvc.perform(delete(VERSION + "/oradb/DIRECTOR")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid delete operation , safe set to true")))
                //.andDo(print())
                .andDo(document("oracle-delete-a-director"));
    }

    @Test
    @DisplayName("Column Does Not Exist")
    void column_does_not_exist() throws Exception {
        mockMvc.perform(delete(VERSION + "/oradb/DIRECTOR")
                        .accept(APPLICATION_JSON)
                        .param("filter", "_name==\"Alex\""))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(jsonPath("$.detail",
                        containsString("Missing column DIRECTOR._name")))
                .andDo(document("oracle-delete-a-director"));
    }

    @Disabled("fix")
    @Test
    @DisplayName("Foreign Key Constraint Violation")
    void foreignKeyConstraintViolation() throws Exception {
        mockMvc.perform(delete(VERSION + "/oradb/LANGUAGE")
                        .accept(APPLICATION_JSON)
                        .param("filter", "name==\"English\""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Cannot delete or update a parent row: a foreign key constraint fails")))
                .andDo(document("oracle-delete-a-director"));
    }
}
