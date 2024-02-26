package com.homihq.db2rest.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!it")
public class JdbcConfiguration {

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        System.out.println("***** creating custom dataSourceProperties ******");
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dataSource() {
        System.out.println("***** creating custom dataSource ******");
        DataSource ds =  dataSourceProperties()
                .initializeDataSourceBuilder()
                .build();

        System.out.println("***** creating custom dataSource ******" + ds.hashCode());

        return ds;
    }
}
