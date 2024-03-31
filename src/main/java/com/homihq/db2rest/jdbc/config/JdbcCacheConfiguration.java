package com.homihq.db2rest.jdbc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.dialect.MySQLDialect;
import com.homihq.db2rest.jdbc.dialect.OracleDialect;
import com.homihq.db2rest.jdbc.dialect.PostGreSQLDialect;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcCacheConfiguration {

    @Bean
    public JdbcSchemaCache schemaCache(DataSource dataSource,  Db2RestConfigProperties db2RestConfigProperties) {
        return new JdbcSchemaCache(dataSource, db2RestConfigProperties);
    }

    @Bean
    public JdbcOperationService operationService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcOperationService(namedParameterJdbcTemplate);
    }

    @Bean
    public Dialect dialect(JdbcSchemaCache jdbcSchemaCache, ObjectMapper objectMapper) {
        if(jdbcSchemaCache.isMySQL()) {
            return new MySQLDialect(objectMapper);
        }
        else if(jdbcSchemaCache.isPostGreSQL()) {
            return new PostGreSQLDialect(objectMapper);
        }
        else if(jdbcSchemaCache.isOracle()) {
            return new OracleDialect(objectMapper, jdbcSchemaCache.getProductName(), jdbcSchemaCache.getProductVersion());
        }
        else {
            throw new BeanCreationException("Unable to create database dialect.");
        }
    }

}
