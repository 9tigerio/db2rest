package com.homihq.db2rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

//@Sql(scripts = {"classpath:pg/postgres-sakila-schema.sql", "classpath:pg/postgres-sakila-insert-data.sql"})
@Import(PostgreSQLContainerConfiguration.class)
public class PostgreSQLBaseIntegrationTest extends BaseIntegrationTest{

    /*
    @ServiceConnection
    private static final PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15-alpine");

    static {
        postgreSQLContainer.withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("postgres").withReuse(true);
    }


    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }

     */
}
