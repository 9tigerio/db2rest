package com.homihq.db2rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgreSQLBaseIntegrationTest extends BaseIntegrationTest{

    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15-alpine");

    static {
        postgreSQLContainer.withUsername("postgres")
                .withPassword("postgres")
                .withInitScript("pg/postgres-sakila-schema.sql") // inside src/test/resources
                .withDatabaseName("postgres");
    }

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }
}
