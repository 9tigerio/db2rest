package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.core.service.ProcedureService;
import com.homihq.db2rest.jdbc.rest.create.BulkCreateController;
import com.homihq.db2rest.jdbc.rest.create.CreateController;
import com.homihq.db2rest.core.bulk.DataProcessor;
import com.homihq.db2rest.jdbc.rest.delete.DeleteController;
import com.homihq.db2rest.jdbc.rest.read.*;
import com.homihq.db2rest.jdbc.rest.rpc.FunctionController;
import com.homihq.db2rest.jdbc.rest.rpc.ProcedureController;
import com.homihq.db2rest.jdbc.rest.update.UpdateController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//@Configuration
@Slf4j
public class RestApiConfiguration {

    public RestApiConfiguration() {
       log.info("Loading rest apis for RDBMS.");
    }


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
}
