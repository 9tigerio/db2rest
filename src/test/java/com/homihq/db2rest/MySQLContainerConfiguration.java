package com.homihq.db2rest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class MySQLContainerConfiguration {

    private static final List<String> mySqlScripts = List.of("mysql/mysql-sakila.sql",
            "mysql/mysql-sakila-data.sql");

    private static final MySQLContainer mySQLContainer = (MySQLContainer) new MySQLContainer("mysql:8.2")
            .withDatabaseName("sakila")
            .withUsername("root")
            //.withPassword("mysql")
            .withReuse(true);

    static {
        mySQLContainer.start();
        var containerDelegate = new JdbcDatabaseDelegate(mySQLContainer, "");
        mySqlScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }


    @Bean("mySQLDataSource")
    @ConditionalOnProperty(prefix = "db2rest.datasource", name="type" , havingValue = "jdbc-mysql")
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url(mySQLContainer.getJdbcUrl());
        dataSourceBuilder.username(mySQLContainer.getUsername());
        dataSourceBuilder.password(mySQLContainer.getPassword());
        return dataSourceBuilder.build();
    }
}
