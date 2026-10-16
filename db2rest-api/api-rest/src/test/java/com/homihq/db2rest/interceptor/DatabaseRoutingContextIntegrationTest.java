package com.homihq.db2rest.interceptor;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import com.homihq.db2rest.multidb.DatabaseContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.Callable;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DatabaseRoutingContextIntegrationTest {

    private SingleConnectionDataSource alpha;
    private SingleConnectionDataSource beta;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        DatabaseContextHolder.clear();
        alpha = database("alpha");
        beta = database("beta");
        RoutingDataSource routing = new RoutingDataSource();
        routing.setTargetDataSources(Map.of("alpha", alpha, "beta", beta));
        routing.setLenientFallback(false);
        routing.afterPropertiesSet();
        DatabaseController controller = new DatabaseController(new JdbcTemplate(routing),
                Map.of("alpha", new JdbcTemplate(alpha), "beta", new JdbcTemplate(beta)));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addInterceptors(new DatabaseContextRequestInterceptor()).build();
    }

    @AfterEach
    void cleanUp() {
        DatabaseContextHolder.clear();
        if (alpha != null) alpha.destroy();
        if (beta != null) beta.destroy();
    }

    @Test
    void sequentialRequestsReadDifferentPhysicalDatabases() throws Exception {
        mockMvc.perform(get(VERSION + "/alpha/marker"))
                .andExpect(status().isOk()).andExpect(content().string("alpha"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
        mockMvc.perform(get(VERSION + "/beta/marker"))
                .andExpect(status().isOk()).andExpect(content().string("beta"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void databaseFailureCannotLeakARouteIntoAdminOrTheNextDatabaseRequest() throws Exception {
        mockMvc.perform(get(VERSION + "/alpha/sql-failure"))
                .andExpect(status().isInternalServerError());
        assertNull(DatabaseContextHolder.getCurrentDbId());
        mockMvc.perform(get("/admin/route"))
                .andExpect(status().isOk()).andExpect(content().string("none"));
        mockMvc.perform(get(VERSION + "/beta/marker"))
                .andExpect(status().isOk()).andExpect(content().string("beta"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    @Test
    void asyncQuerySelectsItsExplicitDatabaseWithoutInheritingServletContext() throws Exception {
        MvcResult result = mockMvc.perform(get(VERSION + "/beta/async-marker"))
                .andExpect(request().asyncStarted()).andReturn();
        assertNull(DatabaseContextHolder.getCurrentDbId());
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk()).andExpect(content().string("beta:none"));
        assertNull(DatabaseContextHolder.getCurrentDbId());
    }

    private SingleConnectionDataSource database(String marker) {
        SingleConnectionDataSource source = new SingleConnectionDataSource("jdbc:sqlite::memory:", true);
        JdbcTemplate jdbc = new JdbcTemplate(source);
        jdbc.execute("CREATE TABLE route_marker (marker TEXT NOT NULL)");
        jdbc.update("INSERT INTO route_marker VALUES (?)", marker);
        return source;
    }

    @RestController
    static class DatabaseController {
        private final JdbcTemplate routedJdbc;
        private final Map<String, JdbcTemplate> databases;

        DatabaseController(JdbcTemplate routedJdbc, Map<String, JdbcTemplate> databases) {
            this.routedJdbc = routedJdbc;
            this.databases = databases;
        }

        @GetMapping(VERSION + "/{dbId}/marker")
        String marker() {
            return routedJdbc.queryForObject("SELECT marker FROM route_marker", String.class);
        }

        @GetMapping(VERSION + "/{dbId}/sql-failure")
        String sqlFailure() {
            return routedJdbc.queryForObject("SELECT marker FROM missing_table", String.class);
        }

        @GetMapping(VERSION + "/{dbId}/async-marker")
        Callable<String> asyncMarker(@PathVariable("dbId") String dbId) {
            // Match JdbcManager's explicit database selection for worker tasks.
            JdbcTemplate jdbc = databases.get(dbId);
            return () -> jdbc.queryForObject("SELECT marker FROM route_marker", String.class)
                    + ":" + currentRoute();
        }

        @GetMapping("/admin/route")
        String admin() {
            return currentRoute();
        }

        @ExceptionHandler(DataAccessException.class)
        ResponseEntity<String> handleDatabaseFailure() {
            return ResponseEntity.internalServerError().body("database failure");
        }

        private static String currentRoute() {
            String dbId = DatabaseContextHolder.getCurrentDbId();
            return dbId == null ? "none" : dbId;
        }
    }
}
