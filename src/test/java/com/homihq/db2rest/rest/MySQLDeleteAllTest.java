package com.homihq.db2rest.rest;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = {"db2rest.allowSafeDelete=false"})
class MySQLDeleteAllTest extends MySQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete all records while allowSafeDelete=false")
    void delete_all_records_with_allow_safe_delete_false() throws Exception {
        mockMvc.perform(delete("/country")
                        //.header("Content-Profile", "sakila")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(4)))
                //.andDo(print())
                .andDo(document("mysql-delete-a-director"));
    }
}
