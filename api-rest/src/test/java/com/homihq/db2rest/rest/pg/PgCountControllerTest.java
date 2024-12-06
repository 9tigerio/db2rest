package com.homihq.db2rest.rest.pg;

import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(102)
@TestWithResources
class PgCountControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Test count")
    void countAll() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/film/count")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andDo(print())
                .andDo(document("pg-get-film-count"));
    }

}
