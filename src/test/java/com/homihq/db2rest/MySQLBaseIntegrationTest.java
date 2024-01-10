package com.homihq.db2rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.ext.ScriptUtils;

@Sql(scripts = {"classpath:mysql/mysql-sakila-schema.sql", "classpath:mysql/mysql-sakila-insert-data.sql"})
public class MySQLBaseIntegrationTest extends BaseIntegrationTest{

    @ServiceConnection
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.2");

    static {
        mySQLContainer
                .withDatabaseName("sakila")
                .withUsername("mysql")
                .withPassword("mysql")
                .withUrlParam("serverTimezone", "UTC")

                .start();

    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }
}
