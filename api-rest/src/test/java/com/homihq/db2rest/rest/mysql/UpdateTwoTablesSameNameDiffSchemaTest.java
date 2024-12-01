package com.homihq.db2rest.rest.mysql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(71)
@TestWithResources
class MySQLUpdateTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/UPDATE_EMPLOYEE_REQUEST.json")
    Map<String, Object> updateEmployeeRequest;

    @Test
    @DisplayName("Update employee diff schema")
    void updateEmployee() throws Exception {
        mockMvc.perform(patch(VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "sakila")
                        .param("filter", "emp_id==1")
                        .content(objectMapper.writeValueAsString(updateEmployeeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("mysql-update-emp-sakila"));

        mockMvc.perform(patch(VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "wakila")
                        .param("filter", "emp_id==1")
                        .content(objectMapper.writeValueAsString(updateEmployeeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                //.andDo(print())
                .andDo(document("mysql-update-emp-wakila"));
    }
}
