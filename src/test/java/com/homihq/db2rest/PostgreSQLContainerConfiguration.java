package com.homihq.db2rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import javax.sql.DataSource;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-pg")
public class PostgreSQLContainerConfiguration {

    private static final List<String> postgresScripts = List.of("pg/postgres-sakila.sql",
            "pg/postgres-sakila-data.sql");

    private static final PostgreSQLContainer testPostgres = (PostgreSQLContainer) new PostgreSQLContainer("postgres:15.2-alpine")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(true);

    static {
        testPostgres.start();
        var containerDelegate = new JdbcDatabaseDelegate(testPostgres, "");
        postgresScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }

    @Bean
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url(testPostgres.getJdbcUrl() + "&escapeSyntaxCallMode=callIfNoReturn");
        dataSourceBuilder.username(testPostgres.getUsername());
        dataSourceBuilder.password(testPostgres.getPassword());
        return dataSourceBuilder.build();
    }

}
