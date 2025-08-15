package com.homihq.db2rest;

import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
@Profile("it-sqlite")
public class SQLiteContainerConfiguration {

    private static final List<String> sqliteScripts = List.of("sqlite/sqlite-sakila.sql",
            "sqlite/sqlite-sakila-data.sql");

    private static final String SQLITE_DB_FILE;

    static {
        try {
            File tempFile = Files.createTempFile("sqlite-test", ".db").toFile();
            tempFile.deleteOnExit();
            SQLITE_DB_FILE = tempFile.getAbsolutePath();
            
            // Initialize database with scripts
            var dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName("org.sqlite.JDBC");
            dataSourceBuilder.url("jdbc:sqlite:" + SQLITE_DB_FILE);
            DataSource initDataSource = dataSourceBuilder.build();
            
            try (Connection connection = initDataSource.getConnection()) {
                for (String script : sqliteScripts) {
                    ScriptUtils.executeSqlScript(connection, new ClassPathResource(script));
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite test database", e);
        }
    }

    @Bean
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:" + SQLITE_DB_FILE);
        DataSource dataSource = dataSourceBuilder.build();

        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(Map.of("sqlitedb", dataSource));
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
    }
}