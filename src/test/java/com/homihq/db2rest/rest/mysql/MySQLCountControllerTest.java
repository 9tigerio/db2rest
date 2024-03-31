package com.homihq.db2rest.rest.mysql;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(2)
class MySQLCountControllerTest extends MySQLBaseIntegrationTest {


    @Test
    @DisplayName("Get count")
    void findFilmCount() throws Exception {
        mockMvc.perform(get("/film/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("mysql-get-film-count"));

    }


}
