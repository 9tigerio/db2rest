package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Write-path coverage for issue #916: quoted mixed-case PostgreSQL table names.
 * Each test touches its own rows in stg."FilmWrites" so execution order does not matter.
 */
class PgCaseSensitiveTableWriteTest extends PostgreSQLBaseIntegrationTest {

    private static final String TABLE_URL = VERSION + "/pgsqldb/FilmWrites";

    @Test
    @DisplayName("Create a row in a quoted mixed-case PostgreSQL table")
    void createInCaseSensitiveTable() throws Exception {
        mockMvc.perform(post(TABLE_URL)
                        .header("Content-Profile", "stg")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("{\"film_id\": 9261, \"title\": \"Created\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)));

        assertTitle(9261, "Created");
    }

    @Test
    @DisplayName("Create a row in a quoted mixed-case PostgreSQL table without a schema header")
    void createInCaseSensitiveTableWithoutSchemaHeader() throws Exception {
        mockMvc.perform(post(TABLE_URL)
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("{\"film_id\": 9267, \"title\": \"Created without schema\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)));

        assertTitle(9267, "Created without schema");
    }

    @Test
    @DisplayName("Bulk create rows in a quoted mixed-case PostgreSQL table")
    void bulkCreateInCaseSensitiveTable() throws Exception {
        mockMvc.perform(post(TABLE_URL + "/bulk")
                        .header("Content-Profile", "stg")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("[{\"film_id\": 9262, \"title\": \"Bulk one\"},"
                                + "{\"film_id\": 9263, \"title\": \"Bulk two\"}]"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows", hasSize(2)));

        assertTitle(9262, "Bulk one");
        assertTitle(9263, "Bulk two");
    }

    @Test
    @DisplayName("Update a row in a quoted mixed-case PostgreSQL table")
    void updateInCaseSensitiveTable() throws Exception {
        mockMvc.perform(patch(TABLE_URL)
                        .header("Content-Profile", "stg")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "film_id==9264")
                        .content("{\"title\": \"Updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)));

        assertTitle(9264, "Updated");
    }

    @Test
    @DisplayName("Update a row in a quoted mixed-case PostgreSQL table without a schema header")
    void updateInCaseSensitiveTableWithoutSchemaHeader() throws Exception {
        mockMvc.perform(patch(TABLE_URL)
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .param("filter", "film_id==9266")
                        .content("{\"title\": \"Updated without schema\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)));

        assertTitle(9266, "Updated without schema");
    }

    @Test
    @DisplayName("Delete a row from a quoted mixed-case PostgreSQL table")
    void deleteFromCaseSensitiveTable() throws Exception {
        mockMvc.perform(delete(TABLE_URL)
                        .header("Content-Profile", "stg")
                        .accept(APPLICATION_JSON)
                        .param("filter", "film_id==9265"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)));

        mockMvc.perform(get(TABLE_URL)
                        .header("Accept-Profile", "stg")
                        .accept(APPLICATION_JSON)
                        .param("filter", "film_id==9265"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    private void assertTitle(int filmId, String expectedTitle) throws Exception {
        mockMvc.perform(get(TABLE_URL)
                        .header("Accept-Profile", "stg")
                        .accept(APPLICATION_JSON)
                        .param("filter", "film_id==" + filmId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value(expectedTitle));
    }
}
