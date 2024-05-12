package com.homihq.db2rest.rest.mariadb;

import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(302)
class MariaDBCountControllerTest extends MariaDBBaseIntegrationTest {


    @Test
    @DisplayName("test film count")
    void testFilmCount() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("mariadb-get-film-count"));

    }

}
