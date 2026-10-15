package com.homihq.db2rest.jdbc;

import com.db2rest.jdbc.dialect.model.ArrayTypeValueHolder;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Array;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PgJdbcArrayResourceTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2-alpine");

    private final JdbcOperationService service = new JdbcOperationService();
    private HikariDataSource pool;
    private JdbcTemplate jdbc;
    private NamedParameterJdbcTemplate namedJdbc;
    private String table;

    @BeforeEach
    void setUp() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(0);
        config.setConnectionTimeout(1000);
        pool = new HikariDataSource(config);
        jdbc = new JdbcTemplate(pool);
        namedJdbc = new NamedParameterJdbcTemplate(pool);
        table = "array_resource_" + UUID.randomUUID().toString().replace("-", "");
        jdbc.execute("CREATE TABLE " + table + " (id SERIAL PRIMARY KEY, tags VARCHAR(12)[], backup_tags VARCHAR(12)[])");
    }

    @AfterEach
    void cleanUp() {
        if (pool != null) {
            try {
                if (table != null) jdbc.execute("DROP TABLE IF EXISTS " + table);
            } finally {
                pool.close();
            }
        }
    }

    @Test
    void repeatedInsertsReturnEveryBorrowedConnection() {
        Object[] values = {"one,two", "quote\"", "বাংলা", null};
        for (int i = 0; i < 12; i++) {
            assertEquals(1, insert("varchar", values).row());
            assertEquals(0, activeConnections(), "array insert must return its connection");
        }
        assertEquals(12, jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class));
        assertArrayEquals(values, readArray("tags"));
    }

    @Test
    void emptyArrayRoundTripsWithoutLeakingConnections() {
        insert("varchar", new Object[0]);
        assertArrayEquals(new Object[0], readArray("tags"));
        assertEquals(0, activeConnections());
    }

    @Test
    void transactionUsesOnePhysicalConnectionAndCommits() {
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(pool));
        transaction.executeWithoutResult(status -> {
            insert("varchar", new Object[]{"committed"});
            assertEquals(1, activeConnections(), "array creation must use the transaction connection");
        });
        assertEquals(0, activeConnections());
        assertArrayEquals(new Object[]{"committed"}, readArray("tags"));
    }

    @Test
    void conversionFailureRollsBackEarlierWriteAndReturnsConnections() {
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(pool));
        GenericDataAccessException failure = assertThrows(GenericDataAccessException.class,
                () -> transaction.executeWithoutResult(status -> {
                    insert("varchar", new Object[]{"before"});
                    insert("missing_array_type_959", new Object[]{"invalid"});
                }));
        assertEquals("Unable to convert Array field", failure.getMessage());
        assertEquals(0, activeConnections());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class));
    }

    @Test
    void statementFailureRollsBackEarlierWriteAndReturnsConnections() {
        TransactionTemplate transaction = new TransactionTemplate(new JdbcTransactionManager(pool));
        assertThrows(RuntimeException.class, () -> transaction.executeWithoutResult(status -> {
            insert("varchar", new Object[]{"before"});
            insert("varchar", new Object[]{"value-exceeds-column-length"});
        }));
        assertEquals(0, activeConnections());
        assertEquals(0, jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class));
    }

    @Test
    void repeatedNamedArrayParameterWritesBothColumns() {
        service.create(namedJdbc, Map.of("tags", holder("varchar", new Object[]{"same"})),
                "INSERT INTO " + table + " (tags, backup_tags) VALUES (:tags, :tags)", null);
        assertArrayEquals(new Object[]{"same"}, readArray("tags"));
        assertArrayEquals(new Object[]{"same"}, readArray("backup_tags"));
        assertEquals(0, activeConnections());
    }

    private com.homihq.db2rest.core.dto.CreateResponse insert(String type, Object[] values) {
        return service.create(namedJdbc, Map.of("tags", holder(type, values)),
                "INSERT INTO " + table + " (tags) VALUES (:tags)", null);
    }

    private ArrayTypeValueHolder holder(String type, Object[] values) {
        return new ArrayTypeValueHolder("java.sql.Array", type, values);
    }

    private int activeConnections() {
        return pool.getHikariPoolMXBean().getActiveConnections();
    }

    private Object[] readArray(String column) {
        return jdbc.queryForObject("SELECT " + column + " FROM " + table + " LIMIT 1", (rs, row) -> {
            Array array = rs.getArray(1);
            try {
                return (Object[]) array.getArray();
            } finally {
                array.free();
            }
        });
    }
}
