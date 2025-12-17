package com.homihq.db2rest;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.testcontainers.db2.Db2Container;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-db2")
public class DB2ContainerConfiguration {

    private static final List<String> db2Scripts = List.of("db2/db2-sakila.sql"
            , "db2/db2-sakila-data.sql");

    private static final Db2Container db2 = new Db2Container("icr.io/db2_community/db2:11.5.8.0").acceptLicense()
            .withDatabaseName("BLUDB")
            .withUsername("db2inst1")
            .withPassword("password")
            .withReuse(true);

    static {
        db2.start();
        var containerDelegate = new JdbcDatabaseDelegate(db2, "");
        db2Scripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }

    @Bean
    public DataSource dataSource() {

        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.ibm.db2.jcc.DB2Driver");
        dataSourceBuilder.url(db2.getJdbcUrl());
        dataSourceBuilder.username(db2.getUsername());
        dataSourceBuilder.password(db2.getPassword());
        DataSource dataSource = dataSourceBuilder.build();

        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Map.of("db2b", dataSource));
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
    }
    
}
