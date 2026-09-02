package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(104)
public class PgReadControllerPaginationValidationTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Reject invalid limit less than -1")
    void rejectInvalidLimit() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/person")
                        .param("limit", "-2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("limit")))
                .andDo(document("pg-reject-invalid-pagination-limit"));
    }

    @Test
    @DisplayName("Reject zero limit")
    void rejectZeroLimit() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/person")
                        .param("limit", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("limit")))
                .andDo(document("pg-reject-zero-pagination-limit"));
    }

    @Test
    @DisplayName("Reject invalid offset less than -1")
    void rejectInvalidOffset() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/person")
                        .param("offset", "-2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", containsString("offset")))
                .andDo(document("pg-reject-invalid-pagination-offset"));
    }
}
