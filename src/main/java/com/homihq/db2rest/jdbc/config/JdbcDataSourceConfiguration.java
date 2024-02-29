package com.homihq.db2rest.jdbc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!it") //integration test profile auto-configures DB with testcontainer
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "jdbc")
public class JdbcDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource() {

        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .build();

    }

}
