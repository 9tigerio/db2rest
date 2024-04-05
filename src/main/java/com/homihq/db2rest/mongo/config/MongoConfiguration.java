package com.homihq.db2rest.mongo.config;

import com.homihq.db2rest.mongo.repository.MongoRepository;
import com.homihq.db2rest.mongo.dialect.MongoDialect;
import com.homihq.db2rest.mongo.rest.MongoController;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "mongo")
public class MongoConfiguration {

    @Bean
    public MongoDialect mongodbDialect() {
        return new MongoDialect();
    }

    @DependsOn("mongoTemplate")
    public MongoRepository mongoRepository(MongoTemplate mongoTemplate) {
        return new MongoRepository(mongoTemplate, mongodbDialect());
    }

    @Bean
    @DependsOn("mongoTemplate")
    public MongoController mongoController(MongoTemplate mongoTemplate) {
        return new MongoController(mongoRepository(mongoTemplate));
    }
}
