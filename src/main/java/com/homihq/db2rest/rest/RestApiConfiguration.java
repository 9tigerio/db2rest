package com.homihq.db2rest.rest;

import com.homihq.db2rest.jdbc.service.JdbcBulkCreateService;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.rest.create.BulkCreateController;
import com.homihq.db2rest.rest.create.CreateController;
import com.homihq.db2rest.rest.create.bulk.DataProcessor;
import com.homihq.db2rest.rest.delete.DeleteController;
import com.homihq.db2rest.jdbc.service.JdbcDeleteService;
import com.homihq.db2rest.rest.read.*;
import com.homihq.db2rest.rest.rpc.FunctionController;
import com.homihq.db2rest.rest.rpc.ProcedureController;
import com.homihq.db2rest.rest.update.UpdateController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RestApiConfiguration {
    //CREATE API
    @Bean
    public BulkCreateController bulkCreateController(JdbcBulkCreateService jdbcBulkCreateService, List<DataProcessor> dataProcessors) {
        return new BulkCreateController(jdbcBulkCreateService, dataProcessors);
    }

    @Bean
    public CreateController createController(JdbcCreateService jdbcCreateService) {
        return new CreateController(jdbcCreateService);
    }

    //READ API
    @Bean
    public CountQueryController countQueryController(JdbcCountQueryService jdbcCountQueryService) {
        return new CountQueryController(jdbcCountQueryService);
    }

    @Bean
    public ExistsQueryController existsQueryController(JdbcExistsQueryService jdbcExistsQueryService) {
        return new ExistsQueryController(jdbcExistsQueryService);
    }

    @Bean
    public CustomQueryController customQueryController(JdbcCustomQueryService jdbcCustomQueryService) {
        return new CustomQueryController(jdbcCustomQueryService);
    }

    @Bean
    public FindOneController findOneController(JdbcFindOneService jdbcFindOneService) {
        return new FindOneController(jdbcFindOneService);
    }

    @Bean
    public ReadController readController(JdbcReadService jdbcReadService) {
        return new ReadController(jdbcReadService);
    }


    //UPDATE API
    @Bean
    public UpdateController updateController(JdbcUpdateService jdbcUpdateService) {
        return new UpdateController(jdbcUpdateService);
    }

    //DELETE API
    @Bean
    public DeleteController deleteController(JdbcDeleteService jdbcDeleteService) {
        return new DeleteController(jdbcDeleteService);
    }

    //RPC
    @Bean
    public FunctionController functionController(JdbcFunctionService jdbcFunctionService) {
        return new FunctionController(jdbcFunctionService);
    }

    @Bean
    public ProcedureController procedureController(JdbcProcedureService jdbcProcedureService) {
        return new ProcedureController(jdbcProcedureService);
    }
}
