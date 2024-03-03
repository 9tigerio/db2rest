package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaManager;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.rsql.operator.handler.OperatorMap;
import com.homihq.db2rest.schema.AliasGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcSchemaManagerConfiguration {

    public JdbcSchemaManagerConfiguration() {
        System.out.println("JdbcSchemaManagerConfiguration");
    }

    @Bean
    public JdbcSchemaManager schemaManager(DataSource dataSource, AliasGenerator aliasGenerator, List<Dialect> dialects) {
        return new JdbcSchemaManager(dataSource, aliasGenerator, dialects);
    }

    @Bean
    public JdbcOperationService operationService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcOperationService(namedParameterJdbcTemplate);
    }

    @Bean
    public JoinProcessor joinProcessor(JdbcSchemaManager jdbcSchemaManager, OperatorMap operatorMap) {
        return new JoinProcessor(jdbcSchemaManager, operatorMap);
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
    public RootTableProcessor rootTableProcessor(JdbcSchemaManager jdbcSchemaManager) {
        return new RootTableProcessor(jdbcSchemaManager);
    }

    @Bean
    public RootWhereProcessor rootWhereProcessor(Dialect dialect) {
        return new RootWhereProcessor(dialect);
    }





}
