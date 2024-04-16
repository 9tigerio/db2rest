package com.homihq.db2rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class MariaDBContainerConfiguration {

    private static final List<String> mariaDbScripts = List.of("mariadb/mariadb-sakila.sql",
            "mariadb/mariadb-sakila-data.sql");

    private static final MariaDBContainer mariaDbContainer = (MariaDBContainer) new MariaDBContainer("mariadb:11.0.3")
            .withDatabaseName("sakila")
            .withUsername("maria")
            .withPassword("test")
            .withReuse(true);

    static {
        mariaDbContainer.start();
        var containerDelegate = new JdbcDatabaseDelegate(mariaDbContainer, "");
        mariaDbScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }


    @Bean("mariaDBDataSource")
    @ConditionalOnProperty(prefix = "db2rest.datasource", name="type" , havingValue = "jdbc-mariadb")
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.mariadb.jdbc.Driver");
        dataSourceBuilder.url(mariaDbContainer.getJdbcUrl());
        dataSourceBuilder.username(mariaDbContainer.getUsername());
        dataSourceBuilder.password(mariaDbContainer.getPassword());
        return dataSourceBuilder.build();
    }
}
