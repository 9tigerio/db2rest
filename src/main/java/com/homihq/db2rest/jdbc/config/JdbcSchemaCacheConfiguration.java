package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.rsql.operator.handler.OperatorMap;
import com.homihq.db2rest.schema.SchemaCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcSchemaCacheConfiguration {

    @Bean
    public JdbcSchemaCache schemaManager(DataSource dataSource) {
        return new JdbcSchemaCache(dataSource);
    }

    @Bean
    public JdbcOperationService operationService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcOperationService(namedParameterJdbcTemplate);
    }

    @Bean
    public JoinProcessor joinProcessor(JdbcSchemaCache jdbcSchemaManager, OperatorMap operatorMap, Dialect dialect) {
        return new JoinProcessor(jdbcSchemaManager, dialect);
    }

    @Bean
    public OrderByProcessor orderByProcessor() {
        return new OrderByProcessor();
    }

    @Bean
    public RootTableFieldProcessor rootTableFieldProcessor() {
        return new RootTableFieldProcessor();
    }

    @Bean
    public RootTableProcessor rootTableProcessor(SchemaCache schemaCache) {
        return new RootTableProcessor(schemaCache);
    }

    @Bean
    public RootWhereProcessor rootWhereProcessor(Dialect dialect) {
        return new RootWhereProcessor(dialect);
    }





}
