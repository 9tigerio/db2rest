package com.homihq.db2rest.jdbc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.dialect.MySQLDialect;
import com.homihq.db2rest.jdbc.dialect.OracleDialect;
import com.homihq.db2rest.jdbc.dialect.PostGreSQLDialect;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.sql.DataSource;


//@Configuration
@Slf4j
@ConditionalOnBean(DataSource.class)
public class JdbcCacheConfiguration {

    /*
    public JdbcCacheConfiguration() {
        log.info("Loading RDBMS schema cache.");
    }

    @Bean
    public JdbcSchemaCache jdbcSchemaCache(DataSource dataSource,  Db2RestConfigProperties db2RestConfigProperties) {
        log.info("JDBC Schema cache is being cached.");
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

    @Bean
    @DependsOn("textTemplateResolver")
    public SqlCreatorTemplate sqlCreatorTemplate(SpringTemplateEngine templateEngine, Dialect dialect, Db2RestConfigProperties db2RestConfigProperties) {
        return new SqlCreatorTemplate(templateEngine, dialect, db2RestConfigProperties);
    }
    */
}
