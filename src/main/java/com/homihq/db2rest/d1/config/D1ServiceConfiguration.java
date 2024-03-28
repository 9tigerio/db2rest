package com.homihq.db2rest.d1.config;


import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.d1.D1Dialect;
import com.homihq.db2rest.d1.service.D1FunctionService;
import com.homihq.db2rest.d1.service.D1ProcedureService;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.schema.SchemaCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import java.util.List;


@Configuration
@ConditionalOnBean(D1Dialect.class)
public class D1ServiceConfiguration {
    //CREATE SERVICE

    @Bean
    public BulkCreateService bulkCreateService(TSIDProcessor tsidProcessor,
                                               SqlCreatorTemplate sqlCreatorTemplate,
                                               SchemaCache schemaCache,
                                               DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcBulkCreateService(tsidProcessor, sqlCreatorTemplate, schemaCache, dbOperationService, dialect);
    }

    @Bean
    public CreateService createService(TSIDProcessor tsidProcessor,
                                       SqlCreatorTemplate sqlCreatorTemplate,
                                       SchemaCache schemaCache,
                                       DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcCreateService(tsidProcessor, sqlCreatorTemplate, schemaCache, dbOperationService, dialect);
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
    SqlCreatorTemplate sqlCreatorTemplate,
    DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcDeleteService(db2RestConfigProperties, schemaCache, sqlCreatorTemplate, dbOperationService, dialect);
    }

    //RPC
    @Bean
    public D1FunctionService d1FunctionService() {
        return new D1FunctionService();
    }

    @Bean
    public D1ProcedureService d1ProcedureService() {
        return new D1ProcedureService();
    }

}
