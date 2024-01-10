package com.homihq.db2rest;

import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = {"classpath:mysql/mysql-sakila.sql", "classpath:mysql/mysql-sakila-insert-data.sql"})
public class MySQLBaseIntegrationTest extends BaseIntegrationTest{
    /*
    @ServiceConnection
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.2");

    static {
        mySQLContainer
                .withDatabaseName("sakila")
                .withUsername("mysql")
                .withPassword("mysql")
                .withUrlParam("serverTimezone", "UTC");


    }


    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

     */
}
