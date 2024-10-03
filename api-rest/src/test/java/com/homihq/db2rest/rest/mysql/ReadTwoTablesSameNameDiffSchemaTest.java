package com.homihq.db2rest.rest.mysql;

import com.homihq.db2rest.MySQLBaseIntegrationTest;
import org.junit.jupiter.api.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(3)
class MySQLReadTwoTablesSameNameDiffSchemaTest extends MySQLBaseIntegrationTest {
    @Test
    @DisplayName("Test find all films - all columns - different schemas.")
    void findUsersInTwoSchemas() throws Exception {

        mockMvc.perform(get(VERSION + "/mysqldb/users")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .header("Accept-Profile", "sakila")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(5)))
                .andDo(document("mysql-get-all-users-diff-schemas-sakila"));


        mockMvc.perform(get(VERSION + "/mysqldb/users")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .header("Accept-Profile", "wakila")

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(5)))
                .andDo(document("mysql-get-all-users-diff-schemas-wakila"));
    }


}
