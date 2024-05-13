package com.homihq.db2rest.mongo.config;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.mongo.rest.MongoController;
import com.homihq.db2rest.multidb.DatabaseConnectionDetail;
import com.homihq.db2rest.multidb.DatabaseProperties;
import com.homihq.db2test.mongo.multidb.RoutingMongoTemplate;
import com.homihq.db2test.mongo.repository.MongoRepository;
import com.homihq.db2test.mongo.rsql.RsqlMongodbAdapter;
import com.homihq.db2test.mongo.rsql.argconverters.NoOpConverter;
import com.homihq.db2test.mongo.rsql.argconverters.StringToQueryValueConverter;
import com.homihq.db2test.mongo.rsql.visitor.ComparisonToCriteriaConverter;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoConfiguration {


    private final DatabaseProperties databaseProperties;

    @Bean
    @ConditionalOnMissingBean(RoutingMongoTemplate.class)
    public RoutingMongoTemplate routingMongoTemplate() {

        RoutingMongoTemplate routingMongoTemplate = new RoutingMongoTemplate();

        if(Objects.isNull(databaseProperties.getDatabases())) return routingMongoTemplate;

        List<DatabaseConnectionDetail> mongoDbs =
        databaseProperties.getDatabases()
                .stream()
                .filter(DatabaseConnectionDetail::isMongo)
                .toList();

        if(!mongoDbs.isEmpty()) {

            for(DatabaseConnectionDetail mongo : mongoDbs){
                routingMongoTemplate.add(mongo.name(),
                        mongoTemplate(mongo.url(), mongo.database()));
            }
        }
        else{
            log.info("*** No MongoDB configured.");
        }

        return routingMongoTemplate;
    }


    private MongoTemplate mongoTemplate(String mongoUri, String databaseName) {
        SimpleMongoClientDatabaseFactory simpleMongoClientDatabaseFactory =
                new SimpleMongoClientDatabaseFactory(
                        MongoClients.create(mongoUri), databaseName
                );

        return new MongoTemplate(simpleMongoClientDatabaseFactory);
    }


    @DependsOn("routingMongoTemplate")
    public MongoRepository mongoRepository() {
        return new MongoRepository(routingMongoTemplate());
    }

    @Bean
    @DependsOn("routingMongoTemplate")
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
