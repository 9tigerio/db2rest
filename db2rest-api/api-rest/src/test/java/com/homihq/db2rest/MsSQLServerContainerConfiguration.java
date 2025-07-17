package com.homihq.db2rest;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-mssql")
public class MsSQLServerContainerConfiguration {

    private static final MSSQLServerContainer<?> MSSQL_SERVER_CONTAINER =
            new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
                    .acceptLicense();

    private static final List<String> SCRIPTS = List.of(
            "mssql/mssql-sakila.sql",
            "mssql/mssql-sakila-data.sql"
    );

    static {
        MSSQL_SERVER_CONTAINER.start();
        var containerDelegate = new JdbcDatabaseDelegate(MSSQL_SERVER_CONTAINER, "");
        SCRIPTS.forEach(script -> ScriptUtils.runInitScript(containerDelegate, script));
    }

    @Bean
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(MSSQL_SERVER_CONTAINER.getJdbcUrl());
        dataSourceBuilder.username(MSSQL_SERVER_CONTAINER.getUsername());
        dataSourceBuilder.password(MSSQL_SERVER_CONTAINER.getPassword());
        DataSource dataSource = dataSourceBuilder.build();

        var routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Map.of("mssql", dataSource));
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
    }

}
