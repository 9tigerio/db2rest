package com.homihq.db2rest.rest;

import com.homihq.db2rest.core.service.*;
import com.homihq.db2rest.core.service.ProcedureService;
import com.homihq.db2rest.jdbc.service.*;
import com.homihq.db2rest.rest.create.BulkCreateController;
import com.homihq.db2rest.rest.create.CreateController;
import com.homihq.db2rest.rest.create.bulk.DataProcessor;
import com.homihq.db2rest.rest.delete.DeleteController;
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
    public BulkCreateController bulkCreateController(BulkCreateService bulkCreateService, List<DataProcessor> dataProcessors) {
        return new BulkCreateController(bulkCreateService, dataProcessors);
    }

    @Bean
    public CreateController createController(CreateService createService) {
        return new CreateController(createService);
    }

    //READ API
    @Bean
    public CountQueryController countQueryController(CountQueryService countQueryService) {
        return new CountQueryController(countQueryService);
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
    public FindOneController findOneController(FindOneService findOneService) {
        return new FindOneController(findOneService);
    }

    @Bean
    public ReadController readController(ReadService readService) {
        return new ReadController(readService);
    }


    //UPDATE API
    @Bean
    public UpdateController updateController(UpdateService updateService) {
        return new UpdateController(updateService);
    }

    //DELETE API
    @Bean
    public DeleteController deleteController(DeleteService deleteService) {
        return new DeleteController(deleteService);
    }

    //RPC
    @Bean
    public FunctionController functionController(FunctionService functionService) {
        return new FunctionController(functionService);
    }

    @Bean
    public ProcedureController procedureController(ProcedureService procedureService) {
        return new ProcedureController(procedureService);
    }
}
