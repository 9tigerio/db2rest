package com.homihq.db2rest.mongodb.config;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.mongodb.DefaultMongodbOperationService;
import com.homihq.db2rest.mongodb.MongodbDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "mongo")
@Import(MongodbServiceConfiguration.class)
public class MongodbConfiguration {

    @Bean
    public MongodbDialect mongodbDialect() {
        return new MongodbDialect();
    }

    @Bean
    public DbOperationService defaultMongodbOperationService(MongoTemplate mongoTemplate) {
        return new DefaultMongodbOperationService(mongoTemplate);
    }

}
