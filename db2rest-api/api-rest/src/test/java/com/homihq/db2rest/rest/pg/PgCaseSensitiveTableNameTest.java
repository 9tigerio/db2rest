package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgCaseSensitiveTableNameTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Read a quoted mixed-case PostgreSQL table using its exact URL name")
    void readCaseSensitiveTableUsingExactName() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/FilmData")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id").value(916))
                .andExpect(jsonPath("$[0].title").value("Case-sensitive table names"));
    }

    @Test
    @DisplayName("Read a quoted mixed-case PostgreSQL table from an explicit schema")
    void readCaseSensitiveTableUsingSchemaHeader() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/FilmData")
                        .header("Accept-Profile", "stg")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id").value(916))
                .andExpect(jsonPath("$[0].title").value("Case-sensitive table names"));
    }
}
