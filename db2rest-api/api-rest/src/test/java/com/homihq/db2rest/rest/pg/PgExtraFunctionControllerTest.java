package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(152)
class PgExtraFunctionControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Execute a function with Input Parameters and a Scalar Return Value")
    void execute() throws Exception {
        var json = """ 
                       {
                           "a": 3,
                           "b": 2
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/add_numbers")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.returnvalue", equalTo(5)))
                .andDo(document("pg-execute-function-int-input-params"));
    }

    //NOTE - not supported
    @Disabled
    @Test
    @DisplayName("Function with Input Parameters and a Table as Output")
    void execute_table_output() throws Exception {
        var json = """ 
                       {
                           "dept_id": 1
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/get_employee_details")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                //.andExpect(jsonPath("$", instanceOf(Map.class)))
                //.andExpect(jsonPath("$.*", hasSize(1)))
                //.andExpect(jsonPath("$.returnvalue", equalTo(5)))
                .andDo(document("pg-execute-function-int-input-params-table-output"));
    }

    @Test
    @DisplayName("Procedure with Input Parameters and No Return Value")
    void execute_update() throws Exception {
        var json = """ 
                       {
                           "emp_id": 1,
                           "new_salary" : 50000
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/procedure/update_employee_salary")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("{}"))
                .andDo(document("pg-execute-procedure-input-params-no-return"));
    }

    //NOTE - not supported
    @Disabled
    @Test
    @DisplayName("Function with INOUT Parameters")
    void execute_io() throws Exception {
        var json = """ 
                       {
                           "value": 1
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/increment_value")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("pg-execute-function-in-out-params"));
    }

    //NOTE - not supported
    @Disabled
    @Test
    @DisplayName("Function with Default Parameters - use default")
    void execute_multiply_numbers() throws Exception {
        var json = """ 
                       {
                           "a": 5
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/multiply_numbers")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                //.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("pg-execute-function-default-value-use"));
    }

    @Test
    @DisplayName("Function with Default Parameters - not use default")
    void execute_multiply_numbers_no_default() throws Exception {
        var json = """ 
                       {
                           "a": 5,
                           "b": 5
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/multiply_numbers")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.returnvalue", equalTo(25)))
                .andDo(document("pg-execute-function-default-value-donot-use"));
    }

    @Test
    @DisplayName("Function with OUT Parameters")
    void execute_fn_out_params() throws Exception {
        var json = """ 
                       {
                           "emp_id": 1
                       }
                """;

        mockMvc.perform(post(VERSION + "/pgsqldb/function/get_employee_info")
                        .characterEncoding(UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.emp_name", is("John Doe")))
                .andExpect(jsonPath("$.emp_salary", equalTo(50000.00)))
                .andDo(document("pg-execute-function-out-params"));
    }
}
