package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Slf4j
//@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcProcessorConfiguration {
    /*
    public JdbcProcessorConfiguration() {
        log.info("Loading RDBMS processors.");
    }
    @Bean
    public TSIDProcessor tsidProcessor() {
        return new TSIDProcessor();
    }

    @Bean
    public JoinProcessor joinProcessor(JdbcSchemaCache jdbcSchemaCache, Dialect dialect) {
        return new JoinProcessor(jdbcSchemaCache, dialect);
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
    public RootTableProcessor rootTableProcessor(JdbcSchemaCache jdbcSchemaCache) {
        return new RootTableProcessor(jdbcSchemaCache);
    }

    @Bean
    public RootWhereProcessor rootWhereProcessor(Dialect dialect) {
        return new RootWhereProcessor(dialect);
    }
    */

}
