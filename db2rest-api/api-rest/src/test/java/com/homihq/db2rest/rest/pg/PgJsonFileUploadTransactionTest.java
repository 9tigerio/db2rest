package com.homihq.db2rest.rest.pg;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.homihq.db2rest.core.exception.BulkUploadException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Transaction and isolation contract of the JSON file upload endpoint: an upload is
 * committed as a whole or not at all, and concurrent uploads never mix destinations
 * or counts.
 */
@Order(182)
@DisplayName("JSON file upload transactions")
class PgJsonFileUploadTransactionTest extends PostgreSQLBaseIntegrationTest {

    private static final int ACTOR_BASE = 900_000;
    private static final int DIRECTOR_BASE = 800_000;
    // bounded so rows from other tests (director ids are TSIDs) are never counted or deleted
    private static final int RANGE = 9_999;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM actor WHERE actor_id BETWEEN ? AND ?", ACTOR_BASE, ACTOR_BASE + RANGE);
        jdbcTemplate.update("DELETE FROM director WHERE director_id BETWEEN ? AND ?", DIRECTOR_BASE, DIRECTOR_BASE + RANGE);
        assertThat(activeConnections()).isZero();
    }

    @Test
    @DisplayName("Overlapping uploads to different tables keep their own rows and counts")
    void overlappingUploadsDoNotInterfere() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            for (int round = 0; round < 5; round++) {
                CyclicBarrier start = new CyclicBarrier(2);
                int actorRows = 1200 + round;
                int directorRows = 700 + round;
                String actors = rows("actor_id", ACTOR_BASE, actorRows, "ACTOR");
                String directors = rows("director_id", DIRECTOR_BASE, directorRows, "DIRECTOR");

                Future<Integer> a = pool.submit(() -> uploadAndCount(start, "actor", actors));
                Future<Integer> d = pool.submit(() -> uploadAndCount(start, "director", directors));

                assertThat(a.get(60, TimeUnit.SECONDS)).isEqualTo(actorRows);
                assertThat(d.get(60, TimeUnit.SECONDS)).isEqualTo(directorRows);

                assertThat(count("actor", "actor_id", ACTOR_BASE)).isEqualTo(actorRows);
                assertThat(count("director", "director_id", DIRECTOR_BASE)).isEqualTo(directorRows);
                assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM actor WHERE actor_id BETWEEN ? AND ? AND first_name <> 'ACTOR'",
                        Integer.class, ACTOR_BASE, ACTOR_BASE + RANGE)).isZero();
                assertThat(jdbcTemplate.queryForObject(
                        "SELECT count(*) FROM director WHERE director_id BETWEEN ? AND ? AND first_name <> 'DIRECTOR'",
                        Integer.class, DIRECTOR_BASE, DIRECTOR_BASE + RANGE)).isZero();
                cleanUp();
            }
        } finally {
            pool.shutdownNow();
        }
    }

    @ParameterizedTest(name = "{0} valid rows before the error")
    @ValueSource(ints = {10, 499, 500, 750, 1000})
    @DisplayName("Malformed JSON anywhere in the file commits nothing")
    void malformedJsonCommitsNothing(int validRows) throws Exception {
        String content = rows("actor_id", ACTOR_BASE, validRows, "ACTOR").replaceAll("]$", ",{\"actor_id\": ]");

        upload("actor", content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", endsWith(BulkUploadException.NOTHING_COMMITTED)));

        assertThat(count("actor", "actor_id", ACTOR_BASE)).isZero();
    }

    @Test
    @DisplayName("A database error in a later chunk rolls back earlier chunks and hides database details")
    void constraintViolationInLaterChunkCommitsNothing() throws Exception {
        String content = rows("actor_id", ACTOR_BASE, 700, "ACTOR")
                .replace("{\"actor_id\":" + (ACTOR_BASE + 650) + ",\"first_name\":\"ACTOR\"",
                        "{\"actor_id\":" + (ACTOR_BASE + 650) + ",\"first_name\":null");

        upload("actor", content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("The database rejected the rows at array index 500 to 699. "
                        + BulkUploadException.NOTHING_COMMITTED))
                .andExpect(jsonPath("$.detail", not(containsString("null value"))));

        assertThat(count("actor", "actor_id", ACTOR_BASE)).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "[]", "{\"actor_id\": 1}", "[1, 2]", "[{\"a\":1}] x"})
    @DisplayName("Empty, non-array and trailing-garbage files return 400")
    void invalidFilesReturnBadRequest(String content) throws Exception {
        upload("actor", content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", endsWith(BulkUploadException.NOTHING_COMMITTED)));
    }

    private Integer uploadAndCount(CyclicBarrier start, String table, String content) throws Exception {
        start.await(30, TimeUnit.SECONDS);
        MvcResult result = upload(table, content).andReturn();
        String body = mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(body, "$.row");
    }

    private ResultActions upload(String table, String content) throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "upload.json", "application/json",
                content.getBytes(StandardCharsets.UTF_8));
        return mockMvc.perform(multipart(VERSION + "/pgsqldb/" + table + "/upload")
                .file(file)
                .accept(APPLICATION_JSON));
    }

    private int count(String table, String idColumn, int base) {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) FROM " + table + " WHERE " + idColumn + " BETWEEN ? AND ?", Integer.class, base, base + RANGE);
    }

    private int activeConnections() {
        DataSource target = dataSource;
        if (target instanceof RoutingDataSource routing) {
            target = routing.getResolvedDataSources().get("pgsqldb");
        }
        return target instanceof HikariDataSource hikari && hikari.getHikariPoolMXBean() != null
                ? hikari.getHikariPoolMXBean().getActiveConnections()
                : 0;
    }

    private static String rows(String idColumn, int base, int count, String firstName) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"").append(idColumn).append("\":").append(base + i)
                    .append(",\"first_name\":\"").append(firstName).append('"')
                    .append(",\"last_name\":\"ROW").append(i).append("\"}");
        }
        return sb.append(']').toString();
    }
}
