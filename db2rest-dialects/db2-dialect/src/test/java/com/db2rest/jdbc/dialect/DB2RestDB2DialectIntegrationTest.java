package com.db2rest.jdbc.dialect;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestConfiguration.class)
@Testcontainers
@ActiveProfiles("it-db2")
@Tag("integration")
class DB2RestDB2DialectIntegrationTest {

    @Container
    static Db2Container db2Container = new Db2Container("ibmcom/db2:11.5.8.0")
            .acceptLicense()
            .withDatabaseName("testdb")
            .withUsername("db2inst1")
            .withPassword("db2inst1");

    @DynamicPropertySource
    static void registerDBProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db2Container::getJdbcUrl);
        registry.add("spring.datasource.username", db2Container::getUsername);
        registry.add("spring.datasource.password", db2Container::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DB2RestDB2Dialect db2Dialect;

    @Test
    void testDatabaseConnection() {
        assertTrue(db2Container.isRunning());
        assertNotNull(jdbcTemplate.queryForObject("SELECT 1 FROM SYSIBM.SYSDUMMY1", Integer.class));
    }

    @Test
    void testDialectIsConfigured() {
        assertNotNull(db2Dialect);
        assertTrue(db2Dialect.isSupportedDb("DB2/UDB", 11));
    }

    @Test
    void testBasicDatabaseOperations() {
        // Create a test table
        jdbcTemplate.execute("CREATE TABLE TEST_TABLE (ID INTEGER NOT NULL, NAME VARCHAR(50), PRIMARY KEY (ID))");
        
        // Insert test data
        jdbcTemplate.update("INSERT INTO TEST_TABLE (ID, NAME) VALUES (?, ?)", 1, "Test Name");
        
        // Query test data
        String name = jdbcTemplate.queryForObject("SELECT NAME FROM TEST_TABLE WHERE ID = ?", String.class, 1);
        assertEquals("Test Name", name);
        
        // Clean up
        jdbcTemplate.execute("DROP TABLE TEST_TABLE");
    }

    @Test
    void testTimestampHandling() {
        // Create a test table with timestamp
        jdbcTemplate.execute("CREATE TABLE TIMESTAMP_TEST (ID INTEGER NOT NULL, CREATED_AT TIMESTAMP, PRIMARY KEY (ID))");
        
        // Insert test data with timestamp
        jdbcTemplate.update("INSERT INTO TIMESTAMP_TEST (ID, CREATED_AT) VALUES (?, CURRENT_TIMESTAMP)", 1);
        
        // Query test data
        Object timestamp = jdbcTemplate.queryForObject("SELECT CREATED_AT FROM TIMESTAMP_TEST WHERE ID = ?", Object.class, 1);
        assertNotNull(timestamp);
        
        // Clean up
        jdbcTemplate.execute("DROP TABLE TIMESTAMP_TEST");
    }
}
