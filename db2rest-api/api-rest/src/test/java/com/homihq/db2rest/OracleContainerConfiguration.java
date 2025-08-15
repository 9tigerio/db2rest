package com.homihq.db2rest;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-oracle")
public class OracleContainerConfiguration {

    private static final List<String> oracleScripts = List.of("oracle/oracle-sakila.sql"
            , "oracle/oracle-sakila-data.sql");

    private static final OracleContainer testOracle10g =
            new OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
                    .withDatabaseName("testDB")
                    .withUsername("testUser")
                    .withPassword("testPassword")
                    .withReuse(true);

    static {
        testOracle10g.start();
        var containerDelegate = new JdbcDatabaseDelegate(testOracle10g, "");
        oracleScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }

    @Bean
    public DataSource dataSource() {

        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("oracle.jdbc.OracleDriver");
        dataSourceBuilder.url(testOracle10g.getJdbcUrl());
        dataSourceBuilder.username(testOracle10g.getUsername());
        dataSourceBuilder.password(testOracle10g.getPassword());
        DataSource dataSource = dataSourceBuilder.build();

        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Map.of("oradb", dataSource));
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
    }

}
