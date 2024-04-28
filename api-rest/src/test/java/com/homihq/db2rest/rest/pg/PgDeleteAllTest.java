package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(191)
@TestPropertySource(properties = {"db2rest.allowSafeDelete=false"})
class PgDeleteAllTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete all records while allowSafeDelete=false")
    void deleteAllWithAllowSafeDeleteFalse() throws Exception {
        mockMvc.perform(delete("/country")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(4)))
                .andDo(document("pg-delete-all-countries"));
    }
}
