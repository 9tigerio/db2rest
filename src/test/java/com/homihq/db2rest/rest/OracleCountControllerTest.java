package com.homihq.db2rest.rest;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import com.homihq.db2rest.Oracle10gBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(202)
class OracleCountControllerTest extends Oracle10gBaseIntegrationTest {


    @Test
    @DisplayName("Get count")
    void findFilmCount() throws Exception {
        mockMvc.perform(get("/FILM/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("oracle-get-film-count"));

    }


}
