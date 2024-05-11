package com.homihq.db2rest;


import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-mariadb")
public class MariaDBContainerConfiguration {

    private static final List<String> mariaDbScripts = List.of("mariadb/mariadb-sakila.sql",
            "mariadb/mariadb-sakila-data.sql");

    private static final MariaDBContainer mariaDbContainer = (MariaDBContainer) new MariaDBContainer("mariadb:11.0")
            .withDatabaseName("sakila")
            .withUsername("maria")
            .withPassword("test")
            .withReuse(true);

    static {
        mariaDbContainer.start();
        var containerDelegate = new JdbcDatabaseDelegate(mariaDbContainer, "");
        mariaDbScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }


    @Bean
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.mariadb.jdbc.Driver");
        dataSourceBuilder.url(mariaDbContainer.getJdbcUrl());
        dataSourceBuilder.username(mariaDbContainer.getUsername());
        dataSourceBuilder.password(mariaDbContainer.getPassword());
        DataSource dataSource =  dataSourceBuilder.build();

        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Map.of("mariadb", dataSource));
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
    }
}
