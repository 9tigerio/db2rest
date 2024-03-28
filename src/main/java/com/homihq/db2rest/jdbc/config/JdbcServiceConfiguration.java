package com.homihq.db2rest.jdbc.config;


import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.*;
import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.core.service.ProcedureService;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.core.service.CreateService;
import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.DeleteCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.jdbc.service.JdbcDeleteService;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.schema.SchemaCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;


@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcServiceConfiguration {
    //CREATE SERVICE

    @Bean
    public BulkCreateService bulkCreateService(TSIDProcessor tsidProcessor,
                                               CreateCreatorTemplate createCreatorTemplate,
                                               SchemaCache schemaCache,
                                               DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcBulkCreateService(tsidProcessor, createCreatorTemplate, schemaCache, dbOperationService, dialect);
    }

    @Bean
    public CreateService createService(TSIDProcessor tsidProcessor,
                                       CreateCreatorTemplate createCreatorTemplate,
                                       SchemaCache schemaCache,
                                       DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcCreateService(tsidProcessor, createCreatorTemplate, schemaCache, dbOperationService, dialect);
    }

    //QUERY SERVICE
    @Bean
    public CountQueryService countQueryService(
                                               SqlCreatorTemplate sqlCreatorTemplate,
                                               List<ReadProcessor> processorList,
                                               DbOperationService dbOperationService) {
        return new JdbcCountQueryService(dbOperationService, processorList, sqlCreatorTemplate);
    }

    @Bean
    public ExistsQueryService existsQueryService(
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcExistsQueryService(dbOperationService, processorList, sqlCreatorTemplate);
    }

    @Bean
    public FindOneService findOneService(
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcFindOneService(sqlCreatorTemplate, processorList, dbOperationService);
    }

    @Bean
    public CustomQueryService customQueryService(DbOperationService dbOperationService) {
        return new JdbcCustomQueryService(dbOperationService);
    }

    @Bean
    public ReadService readService(
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcReadService(dbOperationService, processorList, sqlCreatorTemplate);
    }

    //UPDATE SERVICE
    @Bean
    public UpdateService updateService(
            Db2RestConfigProperties db2RestConfigProperties,
            SchemaCache schemaCache,
            UpdateCreatorTemplate updateCreatorTemplate,
            DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcUpdateService(db2RestConfigProperties, schemaCache, updateCreatorTemplate, dbOperationService, dialect);
    }


    //DELETE SERVICE
    @Bean
    public DeleteService deleteService(
            Db2RestConfigProperties db2RestConfigProperties,
    SchemaCache schemaCache,
    DeleteCreatorTemplate deleteCreatorTemplate,
    DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcDeleteService(db2RestConfigProperties, schemaCache, deleteCreatorTemplate, dbOperationService, dialect);
    }

    //RPC
    @Bean
    public FunctionService functionService(JdbcTemplate jdbcTemplate) {
        return new JdbcFunctionService(jdbcTemplate);
    }

    @Bean
    public ProcedureService procedureService(JdbcTemplate jdbcTemplate) {
        return new JdbcProcedureService(jdbcTemplate);
    }

}
