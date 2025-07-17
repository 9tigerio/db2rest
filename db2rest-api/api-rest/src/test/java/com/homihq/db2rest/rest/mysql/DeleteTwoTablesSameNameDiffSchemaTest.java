package com.homihq.db2rest.rest.mysql;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(92)
class MySQLDeleteTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {

    @Test
    @DisplayName("Delete Employee Same table different database")
    void deleteFromTableWithSameNameDifferentSchema() throws Exception {
        mockMvc.perform(delete(VERSION + "/mysqldb/employee")
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "sakila")
                        .param("filter", "emp_id==1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mysql-delete-emp-sakila"));

        mockMvc.perform(delete(VERSION + "/mysqldb/employee")
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "wakila")
                        .param("filter", "emp_id==1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("mysql-delete-emp-wakila"));
    }
}
