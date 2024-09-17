package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(5)
class MsSQLDeleteControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete a director")
    void deleteSingleRecord() throws Exception {
        mockMvc.perform(delete(getPrefixApiUrl() + "/director")
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name==\"Alex\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(print())
                .andDo(document(DB_NAME + "-delete-a-director"));
    }

    @Test
    @DisplayName("Failed to delete all records while allowSafeDelete=true")
    void failedDeleteAllRecordsWithAllowSafeDeleteTrue() throws Exception {
        mockMvc.perform(delete(getPrefixApiUrl() + "/director")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("Invalid delete operation , safe set to true")))
                .andDo(print())
                .andDo(document(DB_NAME + "-delete-all-director"));
    }

    @Test
    @DisplayName("Failed to delete with column does not exist")
    void failedDeleteWithInvalidColumnName() throws Exception {
        mockMvc.perform(delete(getPrefixApiUrl() + "/director")
                        .accept(APPLICATION_JSON)
                        .param("filter", "_name==\"Alex\""))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail",
                        containsString("Missing column director._name")))
                .andDo(print())
                .andDo(document(DB_NAME + "-column-not-exists"));
    }

    @Test
    @DisplayName("Failed to delete with foreign key constraint violation")
    void failedDeleteWithForeignKeyConstraintViolation() throws Exception {
        mockMvc.perform(delete(getPrefixApiUrl() + "/language")
                        .accept(APPLICATION_JSON)
                        .param("filter", "name==\"ENGLISH\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("The DELETE statement conflicted with the REFERENCE constraint")))
                .andDo(print())
                .andDo(document(DB_NAME + "-constraint-violation"));
    }

}
