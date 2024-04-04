package com.homihq.db2rest.mongodb.config;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.CreateService;
import com.homihq.db2rest.mongodb.MongodbDialect;
import com.homihq.db2rest.mongodb.service.MongodbCreateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(MongodbDialect.class)
public class MongodbServiceConfiguration {

    @Bean
    public CreateService createService(DbOperationService dbOperationService, Dialect dialect) {
        return new MongodbCreateService(dbOperationService, dialect);
    }
}
