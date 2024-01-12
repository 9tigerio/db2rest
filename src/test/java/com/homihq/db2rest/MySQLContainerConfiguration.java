package com.homihq.db2rest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class MySQLContainerConfiguration {
    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        var mySQLContainer = new MySQLContainer("mysql:8.2");
        mySQLContainer.withUsername("mysql")
                .withPassword("mysql")
                .withInitScript("mysql/mysql-sakila.sql")
                .withDatabaseName("sakila").withReuse(true);
        return mySQLContainer;
    }
}
