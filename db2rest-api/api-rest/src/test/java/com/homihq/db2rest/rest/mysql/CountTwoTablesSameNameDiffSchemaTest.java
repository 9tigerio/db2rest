package com.homihq.db2rest.rest.mysql;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(6)
class MySQLCountTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {


    @Test
    @DisplayName("Get count two tables same name diff schema")
    void empCount() throws Exception {
        mockMvc.perform(get(VERSION + "/mysqldb/employee/count")
                        .header("Accept-Profile", "sakila")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document("mysql-employee-count-sakila"));

        mockMvc.perform(get(VERSION + "/mysqldb/employee/count")
                        .header("Accept-Profile", "wakila")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document("mysql-employee-count-wakila"));
    }
}
