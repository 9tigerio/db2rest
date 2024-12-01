package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(103)
@TestPropertySource(properties = {"db2rest.defaultFetchLimit=5"})
class PgReadControllerDefaultFetchLimitTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Get all with default fetch limit set to 5")
    void findAllPersonsWithDefaultFetchLimit5() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/person")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anyOf(hasSize(5))))
                .andDo(document("pg-find-all-persons-with-default-fetch-limit-5"));
    }
}
