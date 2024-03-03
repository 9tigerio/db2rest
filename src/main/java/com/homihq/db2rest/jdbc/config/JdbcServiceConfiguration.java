package com.homihq.db2rest.jdbc.config;


import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.dbop.DbOperationService;
import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.DeleteCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.jdbc.service.DeleteService;
import com.homihq.db2rest.rest.read.sql.QueryCreatorTemplate;
import com.homihq.db2rest.schema.SchemaManager;
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
                                               SchemaManager schemaManager,
                                               DbOperationService dbOperationService) {
        return new BulkCreateService(tsidProcessor, createCreatorTemplate, schemaManager, dbOperationService);
    }

    @Bean
    public CreateService createService(TSIDProcessor tsidProcessor,
                                       CreateCreatorTemplate createCreatorTemplate,
                                       SchemaManager schemaManager,
                                       DbOperationService dbOperationService) {
        return new CreateService(tsidProcessor, createCreatorTemplate, schemaManager, dbOperationService);
    }

    //QUERY SERVICE
    @Bean
    public CountQueryService countQueryService(
                                               QueryCreatorTemplate queryCreatorTemplate,
                                               List<ReadProcessor> processorList,
                                               DbOperationService dbOperationService) {
        return new CountQueryService(dbOperationService, processorList, queryCreatorTemplate);
    }

    @Bean
    public ExistsQueryService existsQueryService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new ExistsQueryService(dbOperationService, processorList, queryCreatorTemplate);
    }

    @Bean
    public FindOneService findOneService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new FindOneService(queryCreatorTemplate, processorList, dbOperationService);
    }

    @Bean
    public CustomQueryService customQueryService(DbOperationService dbOperationService) {
        return new CustomQueryService(dbOperationService);
    }

    @Bean
    public ReadService readService(
            QueryCreatorTemplate queryCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new ReadService(dbOperationService, processorList, queryCreatorTemplate);
    }

    //UPDATE SERVICE
    @Bean
    public UpdateService updateService(
            Db2RestConfigProperties db2RestConfigProperties,
            SchemaManager schemaManager,
            UpdateCreatorTemplate updateCreatorTemplate,
            DbOperationService dbOperationService, Dialect dialect) {
        return new UpdateService(db2RestConfigProperties, schemaManager, updateCreatorTemplate, dbOperationService, dialect);
    }


    //DELETE SERVICE
    @Bean
    public DeleteService deleteService(
            Db2RestConfigProperties db2RestConfigProperties,
    SchemaManager schemaManager,
    DeleteCreatorTemplate deleteCreatorTemplate,
    DbOperationService dbOperationService) {
        return new DeleteService(db2RestConfigProperties, schemaManager, deleteCreatorTemplate, dbOperationService);
    }

    //RPC
    @Bean
    public FunctionService functionService(JdbcTemplate jdbcTemplate) {
        return new FunctionService(jdbcTemplate);
    }

    @Bean
    public ProcedureService procedureService(JdbcTemplate jdbcTemplate) {
        return new ProcedureService(jdbcTemplate);
    }

}
