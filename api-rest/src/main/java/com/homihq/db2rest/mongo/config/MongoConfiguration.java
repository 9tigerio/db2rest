package com.homihq.db2rest.mongo.config;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.mongo.dialect.MongoDialect;
import com.homihq.db2rest.mongo.repository.MongoRepository;
import com.homihq.db2rest.mongo.rest.MongoController;
import com.homihq.db2rest.mongo.rsql.RsqlMongodbAdapter;
import com.homihq.db2rest.mongo.rsql.argconverters.NoOpConverter;
import com.homihq.db2rest.mongo.rsql.argconverters.StringToQueryValueConverter;
import com.homihq.db2rest.mongo.rsql.visitor.ComparisonToCriteriaConverter;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "mongo")
public class MongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    String mongoUri;
    @Value("${spring.data.mongodb.database}")
    String databaseName;

    @Bean
    public MongoTemplate mongoTemplate() {
        SimpleMongoClientDatabaseFactory simpleMongoClientDatabaseFactory =
                new SimpleMongoClientDatabaseFactory(
                        MongoClients.create(mongoUri), databaseName
                );

        return new MongoTemplate(simpleMongoClientDatabaseFactory);
    }

    @Bean
    public MongoDialect mongodbDialect() {
        return new MongoDialect();
    }

    @DependsOn("mongoTemplate")
    public MongoRepository mongoRepository() {
        return new MongoRepository(mongoTemplate(), mongodbDialect());
    }

    @Bean
    @DependsOn("mongoTemplate")
    public MongoController mongoController(Db2RestConfigProperties db2RestConfigProperties) {
        return new MongoController(mongoRepository(), rsqlMongoAdapter(List.of(noOpConverter())),
                db2RestConfigProperties);
    }

    @Bean
    public RsqlMongodbAdapter rsqlMongoAdapter(List<StringToQueryValueConverter> converters) {
        return new RsqlMongodbAdapter(comparisonToCriteriaConverter(converters));
    }

    @Bean
    public ComparisonToCriteriaConverter comparisonToCriteriaConverter(List<StringToQueryValueConverter> converters) {
        return new ComparisonToCriteriaConverter(converters);
    }

    @Bean
    public NoOpConverter noOpConverter() {
        return new NoOpConverter();
    }
}
