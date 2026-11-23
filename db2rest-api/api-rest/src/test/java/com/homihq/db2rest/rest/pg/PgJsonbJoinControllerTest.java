package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Joins on a key inside a JSONB column, e.g. {@code tbl_jsonb.data ->> 'key_1' = tbl_translation.key}.
 */
@Order(111)
class PgJsonbJoinControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Inner join on a JSONB key")
    void innerJoinOnJsonbKey() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/tbl_jsonb/_expand")
                        .param("fields", "id,data**key_1:translation_key")
                        .param("sort", "id")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("""
                                [{"table": "tbl_translation", "fields": ["value"],
                                  "joinType": "INNER", "on": ["data**key_1==key"]}]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].translation_key", equalTo("greeting")))
                .andExpect(jsonPath("$[0].value", equalTo("Hello")))
                .andExpect(jsonPath("$[1].id", equalTo(2)))
                .andExpect(jsonPath("$[1].value", equalTo("Goodbye")))
                .andDo(document("pgsql-inner-join-on-jsonb-key"));
    }

    @Test
    @DisplayName("Left join on a JSONB key keeps rows without a match")
    void leftJoinOnJsonbKey() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/tbl_jsonb/_expand")
                        .param("fields", "id")
                        .param("sort", "id")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("""
                                [{"table": "tbl_translation", "fields": ["value"],
                                  "joinType": "LEFT", "on": ["data->>'key_1'==key"]}]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].id", equalTo(3)))
                .andExpect(jsonPath("$[2].value", nullValue()));
    }

    @Test
    @DisplayName("JSONB join combined with a JSONB filter on the root table")
    void jsonbJoinWithJsonbFilter() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/tbl_jsonb/_expand")
                        .param("fields", "id")
                        .param("filter", "data**brand=jbe=Apple")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("""
                                [{"table": "tbl_translation", "fields": ["value"],
                                  "joinType": "INNER", "on": ["data**key_1==key"]}]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].value", equalTo("Hello")));
    }
}
