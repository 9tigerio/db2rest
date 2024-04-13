package com.homihq.db2rest.jdbc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.bulk.DataProcessor;
import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.dialect.MySQLDialect;
import com.homihq.db2rest.jdbc.dialect.OracleDialect;
import com.homihq.db2rest.jdbc.dialect.PostGreSQLDialect;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.rest.create.BulkCreateController;
import com.homihq.db2rest.jdbc.rest.create.CreateController;
import com.homihq.db2rest.jdbc.rest.delete.DeleteController;
import com.homihq.db2rest.jdbc.rest.read.*;
import com.homihq.db2rest.jdbc.rest.rpc.FunctionController;
import com.homihq.db2rest.jdbc.rest.rpc.ProcedureController;
import com.homihq.db2rest.jdbc.rest.schema.SchemaController;
import com.homihq.db2rest.jdbc.rest.update.UpdateController;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.SchemaCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "jdbc")
public class JdbcConfiguration {

    public JdbcConfiguration() {
        log.info("Loading datasource configuration.");
    }

    @Bean
    @ConfigurationProperties("spring.datasource")
    @ConditionalOnMissingBean(DataSource.class)
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {
        log.info("Returning datasource.");
        return dataSourceProperties()
                .initializeDataSourceBuilder()
                .build();

    }

    @Bean
    public JdbcSchemaCache jdbcSchemaCache(DataSource dataSource, Db2RestConfigProperties db2RestConfigProperties) {
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


    //START ::: Processors
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

    //END ::: Processors


    //START ::: Service
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
            SchemaCache schemaCache,
            SqlCreatorTemplate sqlCreatorTemplate,
            DbOperationService dbOperationService, Dialect dialect) {
        return new JdbcUpdateService(schemaCache, sqlCreatorTemplate, dbOperationService, dialect);
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
    public FunctionService functionService(JdbcTemplate jdbcTemplate) {
        return new JdbcFunctionService(jdbcTemplate);
    }

    @Bean
    public ProcedureService procedureService(JdbcTemplate jdbcTemplate) {
        return new JdbcProcedureService(jdbcTemplate);
    }

    //END ::: Services

    //START ::: API

    //CREATE API


    @Bean
    @ConditionalOnBean(BulkCreateService.class)
    public BulkCreateController bulkCreateController(BulkCreateService bulkCreateService, List<DataProcessor> dataProcessors) {
        return new BulkCreateController(bulkCreateService, dataProcessors);
    }


    @Bean
    @ConditionalOnBean(CreateService.class)
    public CreateController createController(CreateService createService) {
        return new CreateController(createService);
    }

    //READ API
    @Bean
    @ConditionalOnBean(CountQueryService.class)
    public CountQueryController countQueryController(CountQueryService countQueryService) {
        return new CountQueryController(countQueryService);
    }

    @Bean
    @ConditionalOnBean(ExistsQueryService.class)
    public ExistsQueryController existsQueryController(ExistsQueryService existsQueryService) {
        return new ExistsQueryController(existsQueryService);
    }

    @Bean
    @ConditionalOnBean(CustomQueryService.class)
    public CustomQueryController customQueryController(CustomQueryService customQueryService) {
        return new CustomQueryController(customQueryService);
    }

    @Bean
    @ConditionalOnBean(FindOneService.class)
    public FindOneController findOneController(FindOneService findOneService) {
        return new FindOneController(findOneService);
    }

    @Bean
    @ConditionalOnBean(ReadService.class)
    public ReadController readController(ReadService readService) {
        return new ReadController(readService);
    }


    //UPDATE API
    @Bean
    @ConditionalOnBean(UpdateService.class)
    public UpdateController updateController(UpdateService updateService) {
        return new UpdateController(updateService);
    }

    //DELETE API
    @Bean
    @ConditionalOnBean(DeleteService.class)
    public DeleteController deleteController(DeleteService deleteService) {
        return new DeleteController(deleteService);
    }

    //RPC
    @Bean
    @ConditionalOnBean(FunctionService.class)
    public FunctionController functionController(FunctionService functionService) {
        return new FunctionController(functionService);
    }

    @Bean
    @ConditionalOnBean(ProcedureService.class)
    public ProcedureController procedureController(ProcedureService procedureService) {
        return new ProcedureController(procedureService);
    }

    @Bean
    @ConditionalOnBean(JdbcSchemaCache.class)
    public SchemaController schemaController(JdbcSchemaCache jdbcSchemaCache) {
        return new SchemaController(jdbcSchemaCache);
    }

    //END ::: API

}
