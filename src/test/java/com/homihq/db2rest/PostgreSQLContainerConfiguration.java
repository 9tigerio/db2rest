package com.homihq.db2rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class PostgreSQLContainerConfiguration {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:15.2-alpine");
        postgreSQLContainer.withUsername("postgres")
                .withPassword("postgres")
                .withInitScript("pg/postgres-sakila.sql")
                .withDatabaseName("postgres").withReuse(true);
        return postgreSQLContainer;

    }
}
