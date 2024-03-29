package com.homihq.db2rest.jdbc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.jdbc.dialect.MySQLDialect;
import com.homihq.db2rest.jdbc.dialect.OracleDialect;
import com.homihq.db2rest.jdbc.dialect.PostGreSQLDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
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

    @Bean
    @ConditionalOnExpression("#{'${spring.datasource.url}'.contains('oracle') or '${db2rest.datasource.type}'.contains('jdbc-orcl')}")
    public OracleDialect oracleDialect(ObjectMapper objectMapper) {
        return new OracleDialect(objectMapper);
    }

}
