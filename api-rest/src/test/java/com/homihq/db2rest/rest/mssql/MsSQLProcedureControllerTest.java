package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(1)
class MsSQLProcedureControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Execute stored procedure on microsoft sql server")
    void execute() throws Exception {
        var json = """ 
                       {
                           "movieTitle": "ACADEMY DINOSAUR"
                       }
                """;

        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetMovieRentalRateProc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.rentalRate", equalTo(0.99)))
                .andDo(print())
                .andDo(document(DB_NAME + "-execute-procedure"));
    }
}
