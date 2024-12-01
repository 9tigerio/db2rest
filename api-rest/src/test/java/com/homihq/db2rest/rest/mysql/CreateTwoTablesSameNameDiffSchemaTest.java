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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(82)
@TestWithResources
class MySQLCreateTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {
    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @GivenJsonResource("/testdata/CREATE_EMP_REQUEST.json")
    Map<String, Object> createEmpRequest;

    @Test
    @DisplayName("Create emp diff schema")
    void create() throws Exception {
        mockMvc.perform(post(VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "sakila")
                        .content(objectMapper.writeValueAsString(createEmpRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.GENERATED_KEY").exists())
                .andExpect(jsonPath("$.keys.GENERATED_KEY", equalTo(3)))
                .andDo(document("mysql-create-emp-sakila"));

        mockMvc.perform(post(VERSION + "/mysqldb/employee")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .header("Content-Profile", "wakila")
                        .content(objectMapper.writeValueAsString(createEmpRequest))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.keys.GENERATED_KEY").exists())
                .andExpect(jsonPath("$.keys.GENERATED_KEY", equalTo(3)))
                .andDo(document("mysql-create-emp-wakila"));
    }
}
