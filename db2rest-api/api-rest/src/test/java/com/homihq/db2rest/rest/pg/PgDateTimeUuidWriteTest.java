package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(115)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PgDateTimeUuidWriteTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @Order(1)
    @DisplayName("Create a row with date, time and uuid columns")
    void createWithDateTimeUuid() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/tbl_schedule")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("""
                                {"start_date": "2024-02-03", "start_time": "11:00:00",
                                 "ref": "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)));
    }

    @Test
    @Order(2)
    @DisplayName("Update a date column")
    void updateDate() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/tbl_schedule")
                        .param("filter", "ref==b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content("""
                                {"start_date": "2025-05-05"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)));

        mockMvc.perform(get(VERSION + "/pgsqldb/tbl_schedule")
                        .param("filter", "ref==b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start_date", equalTo("2025-05-05")))
                .andExpect(jsonPath("$[0].start_time", equalTo("11:00:00")));
    }
}
