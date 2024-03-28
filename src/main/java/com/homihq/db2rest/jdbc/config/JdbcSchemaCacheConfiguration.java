package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.SchemaCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.sql.DataSource;

@Configuration
public class JdbcSchemaCacheConfiguration {


    @Bean
    public JdbcSchemaCache schemaCache(DataSource dataSource) {
        return new JdbcSchemaCache(dataSource);
    }

    @Bean
    public JdbcOperationService operationService(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new JdbcOperationService(namedParameterJdbcTemplate);
    }

    @Bean
    public TSIDProcessor tsidProcessor() {
        return new TSIDProcessor();
    }

    @Bean
    public JoinProcessor joinProcessor(JdbcSchemaCache jdbcSchemaManager, Dialect dialect) {
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

    @Bean
    @DependsOn("textTemplateResolver")
    public SqlCreatorTemplate sqlCreatorTemplate(SpringTemplateEngine templateEngine, Dialect dialect) {
        return new SqlCreatorTemplate(templateEngine, dialect);
    }


}
