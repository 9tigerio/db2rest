package com.homihq.db2rest.config;

import com.homihq.db2rest.bulk.DataProcessor;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.service.*;
import com.homihq.db2rest.rest.admin.AdminController;
import com.homihq.db2rest.rest.create.BulkCreateController;
import com.homihq.db2rest.rest.create.CreateController;
import com.homihq.db2rest.rest.delete.DeleteController;
import com.homihq.db2rest.rest.meta.db.DbInfoController;
import com.homihq.db2rest.rest.meta.schema.SchemaController;
import com.homihq.db2rest.rest.read.CountQueryController;
import com.homihq.db2rest.rest.read.ExistsQueryController;
import com.homihq.db2rest.rest.read.FindOneController;
import com.homihq.db2rest.rest.read.ReadController;
import com.homihq.db2rest.rest.rpc.FunctionController;
import com.homihq.db2rest.rest.rpc.ProcedureController;
import com.homihq.db2rest.rest.sql.SQLTemplateController;
import com.homihq.db2rest.rest.update.UpdateController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Slf4j
@Configuration
public class RestApiConfiguration {


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
    @ConditionalOnBean(FindOneService.class)
    public FindOneController findOneController(FindOneService findOneService) {
        return new FindOneController(findOneService);
    }

    @Bean
    @ConditionalOnBean(ReadService.class)
    public ReadController readController(ReadService readService, Db2RestConfigProperties configProperties) {
        return new ReadController(readService, configProperties);
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
    public DeleteController deleteController(DeleteService deleteService, Db2RestConfigProperties configProperties) {
        return new DeleteController(deleteService, configProperties);
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
    @ConditionalOnBean(JdbcManager.class)
    public SchemaController schemaController(JdbcManager jdbcManager) {
        return new SchemaController(jdbcManager);
    }

    @Bean
    @ConditionalOnBean(SQLTemplateExecutorService.class)
    public SQLTemplateController sqlTemplateController(
            SQLTemplateExecutorService sqlTemplateExecutorService
    ) {
        return new SQLTemplateController(sqlTemplateExecutorService);
    }

    @Bean
    @ConditionalOnBean(JdbcManager.class)
    public DbInfoController dbInfoController(JdbcManager jdbcManager) {
        return new DbInfoController(jdbcManager);
    }


    @Bean
    @ConditionalOnBean(JdbcManager.class)
    public AdminController adminController(JdbcManager jdbcManager) {
        return new AdminController(jdbcManager);
    }
    //END ::: API

}
