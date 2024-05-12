package com.homihq.db2rest.rest.oracle;

import com.homihq.db2rest.OracleBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(202)
class OracleCountControllerTest extends OracleBaseIntegrationTest {


    @Test
    @DisplayName("Get count")
    void findFilmCount() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("oracle-get-film-count"));

    }


}
