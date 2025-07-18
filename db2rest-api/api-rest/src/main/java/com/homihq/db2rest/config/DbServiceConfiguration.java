package com.homihq.db2rest.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.bulk.DataProcessor;
import com.homihq.db2rest.bulk.FileSubject;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.JdbcOperationService;
import com.homihq.db2rest.jdbc.config.dialect.*;
import com.homihq.db2rest.config.jinjava.DisabledExpressionTokenScannerSymbols;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.core.service.*;
import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import com.homihq.db2rest.jdbc.processor.*;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.jdbc.validator.CustomPlaceholderValidators;
import com.homihq.db2rest.multidb.DatabaseConnectionDetail;
import com.homihq.db2rest.multidb.DatabaseProperties;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.CodeResolver;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DbServiceConfiguration {

    private final DatabaseProperties databaseProperties;
    private final ObjectMapper objectMapper;

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource dataSource() {

        final Map<Object, Object> dataSources = this.buildDataSources();

        final RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(dataSources);

        return routingDataSource;
    }

    private Map<Object, Object> buildDataSources() {
        final Map<Object, Object> result = new HashMap<>();

        log.debug("Databases - {}", databaseProperties.getDatabases());

        if (!databaseProperties.isRdbmsConfigured()) {
            log.info("*** No RDBMS configured.");
            return result;
        }


        for (DatabaseConnectionDetail connectionDetail : databaseProperties.getDatabases()) {

            if (connectionDetail.isJdbcPresent())
                result.put(connectionDetail.id(), this.buildDataSource(connectionDetail));
        }

        return result;
    }


    private DataSource buildDataSource(DatabaseConnectionDetail connectionDetail) {
        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl(connectionDetail.url());
        config.setUsername(connectionDetail.username());
        config.setPassword(connectionDetail.password());
        config.setMaximumPoolSize(connectionDetail.maxConnections());

        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcManager jdbcManager() {

        List<Dialect> dialects = List.of(
                new PostGreSQLDialect(objectMapper),
                new MySQLDialect(objectMapper),
                new MariaDBDialect(objectMapper),
                new OracleDialect(objectMapper),
                new MsSQLServerDialect(objectMapper)
        );

        return new JdbcManager(dataSource(), dialects, databaseProperties);
    }

    @Bean
    public JdbcOperationService operationService() {
        return new JdbcOperationService();
    }


    @Bean
    public SqlCreatorTemplate sqlCreatorTemplate(TemplateEngine templateEngine, JdbcManager jdbcManager) {
        return new SqlCreatorTemplate(templateEngine, jdbcManager);
    }

    @Bean
    public TemplateEngine templateEngine() {
        CodeResolver codeResolver =
                new ResourceCodeResolver("sql-templates");
        return TemplateEngine
                .createPrecompiled(ContentType.Plain);
    }

    @Bean
    public Jinjava jinjava() {
        JinjavaConfig config = JinjavaConfig
                .newBuilder()
                .withTokenScannerSymbols(new DisabledExpressionTokenScannerSymbols())
                .build();

        return new Jinjava(config);
    }

    //START ::: Processors
    @Bean
    public TSIDProcessor tsidProcessor() {
        return new TSIDProcessor();
    }

    @Bean
    public JoinProcessor joinProcessor(JdbcManager jdbcManager) {
        return new JoinProcessor(jdbcManager);
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
    public RootTableProcessor rootTableProcessor(JdbcManager jdbcManager) {
        return new RootTableProcessor(jdbcManager);
    }

    @Bean
    public RootWhereProcessor rootWhereProcessor(JdbcManager jdbcManager) {
        return new RootWhereProcessor(jdbcManager);
    }

    //END ::: Processors

    //START ::: Validator
    @Bean
    public CustomPlaceholderValidators customPlaceholderValidators() {
        return new CustomPlaceholderValidators();
    }

    //END ::: Validator

    //START ::: Service
    //CREATE SERVICE

    @Bean
    public BulkCreateService bulkCreateService(TSIDProcessor tsidProcessor,
                                               SqlCreatorTemplate sqlCreatorTemplate,
                                               JdbcManager jdbcManager,
                                               DbOperationService dbOperationService,
                                               List<DataProcessor> dataProcessors,
                                               FileSubject fileSubject) {
        return new JdbcBulkCreateService(tsidProcessor, sqlCreatorTemplate, jdbcManager, dbOperationService, fileSubject);
    }

    @Bean
    public CreateService createService(TSIDProcessor tsidProcessor,
                                       SqlCreatorTemplate sqlCreatorTemplate,
                                       JdbcManager jdbcManager,
                                       DbOperationService dbOperationService) {
        return new JdbcCreateService(tsidProcessor, sqlCreatorTemplate, jdbcManager, dbOperationService);
    }

    //QUERY SERVICE
    @Bean
    public CountQueryService countQueryService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcCountQueryService(
                jdbcManager,
                dbOperationService, processorList, sqlCreatorTemplate);
    }

    @Bean
    public ExistsQueryService existsQueryService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcExistsQueryService(jdbcManager, dbOperationService, processorList, sqlCreatorTemplate);
    }

    @Bean
    public FindOneService findOneService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcFindOneService(jdbcManager, sqlCreatorTemplate, processorList, dbOperationService);
    }

    @Bean
    public ReadService readService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            List<ReadProcessor> processorList,
            DbOperationService dbOperationService) {
        return new JdbcReadService(jdbcManager, dbOperationService, processorList, sqlCreatorTemplate);
    }

    //UPDATE SERVICE
    @Bean
    public UpdateService updateService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            DbOperationService dbOperationService) {
        return new JdbcUpdateService(jdbcManager, sqlCreatorTemplate, dbOperationService);
    }


    //DELETE SERVICE
    @Bean
    public DeleteService deleteService(
            JdbcManager jdbcManager,
            SqlCreatorTemplate sqlCreatorTemplate,
            DbOperationService dbOperationService) {
        return new JdbcDeleteService(jdbcManager, sqlCreatorTemplate, dbOperationService);
    }

    //RPC
    @Bean
    public FunctionService functionService(JdbcManager jdbcManager) {
        return new JdbcFunctionService(jdbcManager);
    }

    @Bean
    public ProcedureService procedureService(JdbcManager jdbcManager) {
        return new JdbcProcedureService(jdbcManager);
    }

    @Bean
    public SQLTemplateExecutorService templateService(
            Jinjava jinjava,
            Db2RestConfigProperties db2RestConfigProperties,
            DbOperationService dbOperationService,
            JdbcManager jdbcManager,
            CustomPlaceholderValidators customPlaceholderValidators
    ) {
        return new JinJavaTemplateExecutorService(
                jinjava,
                db2RestConfigProperties,
                dbOperationService,
                jdbcManager,
                customPlaceholderValidators
        );
    }

    //END ::: Services


}
