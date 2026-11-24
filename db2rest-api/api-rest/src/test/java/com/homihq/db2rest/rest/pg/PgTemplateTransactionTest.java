package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Transaction behaviour and path handling of SQL templates, checked against the database
 * through a connection that is separate from the one the request used.
 */
@Order(103)
@DisplayName("SQL template transactions and path handling")
class PgTemplateTransactionTest extends PostgreSQLBaseIntegrationTest {

    private static final int ACTOR_ID = 990_001;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM actor WHERE actor_id = ?", ACTOR_ID);
        assertThat(activeConnections()).isZero();
    }

    @Test
    @DisplayName("Template insert and delete are visible from another connection once committed")
    void insertAndDeleteAreCommitted() throws Exception {
        try (Connection observer = targetDataSource().getConnection()) {
            assertThat(actorExists(observer)).isFalse();

            mockMvc.perform(post(VERSION + "/pgsqldb/sql/insert_actor_from_content")
                            .content("{\"actor_id\": " + ACTOR_ID + ", \"first_name\": \"TPL\", \"last_name\": \"COMMIT\"}")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.row").value(1));

            assertThat(actorExists(observer)).isTrue();

            mockMvc.perform(post(VERSION + "/pgsqldb/sql/delete_actor_from_content")
                            .content("{\"actor_id\": " + ACTOR_ID + "}")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.rows").value(1));

            assertThat(actorExists(observer)).isFalse();
        }
    }

    @Test
    @DisplayName("A failing template insert leaves nothing behind and does not leak the database error")
    void failedInsertIsRolledBack() throws Exception {
        try (Connection observer = targetDataSource().getConnection()) {
            mockMvc.perform(post(VERSION + "/pgsqldb/sql/insert_actor_from_content")
                            .content("{\"actor_id\": " + ACTOR_ID + ", \"first_name\": null, \"last_name\": \"ROLLBACK\"}")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detail").value("Template insert_actor_from_content failed: the database "
                            + "rejected the statement. The transaction was rolled back."))
                    .andExpect(jsonPath("$.detail", not(containsString("null value"))));

            assertThat(actorExists(observer)).isFalse();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "..%2F..%2Fetc%2Fpasswd",
            "%2e%2e%2fsecret",
            "..%5Csecret",
            "%2Fetc%2Fpasswd",
            "C:%5CWindows%5Cwin",
            "select_all%00",
            "select_all.",
            ".select_all",
            "CON",
            "select%E2%88%95all"
    })
    @DisplayName("Path-like and encoded template names are rejected before any file is read")
    void pathLikeNamesAreRejected(String encodedName) throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/sql/" + encodedName).accept(APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    private boolean actorExists(Connection connection) throws Exception {
        try (PreparedStatement ps = connection.prepareStatement("SELECT count(*) FROM actor WHERE actor_id = ?")) {
            ps.setInt(1, ACTOR_ID);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 1;
            }
        }
    }

    private DataSource targetDataSource() {
        if (dataSource instanceof RoutingDataSource routing) {
            return routing.getResolvedDataSources().get("pgsqldb");
        }
        return dataSource;
    }

    private int activeConnections() {
        return targetDataSource() instanceof HikariDataSource hikari && hikari.getHikariPoolMXBean() != null
                ? hikari.getHikariPoolMXBean().getActiveConnections()
                : 0;
    }
}
