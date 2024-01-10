package com.homihq.db2rest.rest;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MySQLReadControllerTest extends MySQLBaseIntegrationTest {
    @Test
    @DisplayName("Get all fields.")
    void findAllFilms() throws Exception {

        mockMvc.perform(get("/films")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(document("get-all-films"));
    }
}
