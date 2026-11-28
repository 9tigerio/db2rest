package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(117)
class PgNullArrayReadTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Read a varchar[] column that holds NULL")
    void readNullVarcharArray() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/tbl_aliases").param("sort", "id").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].names", contains("a", "b")))
                .andExpect(jsonPath("$[1].names", nullValue()));
    }
}
