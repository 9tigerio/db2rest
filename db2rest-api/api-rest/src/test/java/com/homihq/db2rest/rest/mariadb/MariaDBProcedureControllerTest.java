package com.homihq.db2rest.rest.mariadb;

import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(350)
class MariaDBProcedureControllerTest extends MariaDBBaseIntegrationTest {

    @Test
    @DisplayName("Execute stored procedure on maria db")
    void execute() throws Exception {
        var json = """ 
                       {
                           "movieTitle": "ACADEMY DINOSAUR"
                       }
                """;

        mockMvc.perform(post(VERSION + "/mariadb/procedure/GetMovieRentalRateProc")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.rentalRate", equalTo(0.99)))
                //.andDo(print())
                .andDo(document("mariadb-execute-procedure"));
    }
}
