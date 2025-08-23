package com.homihq.db2rest.rest.mariadb;

import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(351)
class MariaDBFunctionControllerTest extends MariaDBBaseIntegrationTest {
    @Test
    @DisplayName("Execute function on maria db")
    void execute() throws Exception {
        var json = """ 
                       {
                           "movieTitle": "ACADEMY DINOSAUR"
                       }
                """;

        mockMvc.perform(post(VERSION + "/mariadb/function/GetMovieRentalRateFunc")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.return", equalTo(0.99)))
                //.andDo(print())
                .andDo(document("mariadb-execute-function"));
    }

    @Test
    @DisplayName("Execute UpdateUserFunc on MariaDB")
    void updateUser() throws Exception {
        var json = """ 
                       {
                           "user_id": "2"
                       }
                """;
        mockMvc.perform(post(VERSION + "/mariadb/function/UpdateUserFunc")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$['return']", equalTo(1)));
    }
}
