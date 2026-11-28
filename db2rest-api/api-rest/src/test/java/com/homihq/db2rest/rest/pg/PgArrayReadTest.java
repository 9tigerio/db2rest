package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.contains;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(116)
class PgArrayReadTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Read text[] and integer[] columns as JSON arrays")
    void readTextAndIntegerArrays() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/tbl_tags").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].labels", contains("red", "green")))
                .andExpect(jsonPath("$[0].scores", contains(1, 2)));
    }
}
