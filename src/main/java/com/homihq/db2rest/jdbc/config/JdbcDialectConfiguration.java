package com.homihq.db2rest.jdbc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.jdbc.dialect.MySQLDialect;
import com.homihq.db2rest.jdbc.dialect.PostGreSQLDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcDialectConfiguration {

    @Bean
    @ConditionalOnExpression("#{'${spring.datasource.url}'.contains('mysql') or '${db2rest.datasource.type}'.equals('jdbc-mysql') }")
    public MySQLDialect mySQLDialect(ObjectMapper objectMapper) {
        return new MySQLDialect(objectMapper);
    }

    @Bean
    @ConditionalOnExpression("#{'${spring.datasource.url}'.contains('postgresql') or '${db2rest.datasource.type}'.equals('jdbc-pg')}")
    public PostGreSQLDialect postGreSQLDialect(ObjectMapper objectMapper) {
        return new PostGreSQLDialect(objectMapper);
    }

}
