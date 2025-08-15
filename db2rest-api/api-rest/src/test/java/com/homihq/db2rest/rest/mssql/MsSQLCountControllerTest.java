package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(501)
class MsSQLCountControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("The count of employees")
    void employeesCount() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/employee/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document(DB_NAME + "-employees-count"));
    }

    @Test
    @DisplayName("The count of films")
    void filmsCount() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", equalTo(4)))
                .andDo(document(DB_NAME + "-films-count"));
    }
}
