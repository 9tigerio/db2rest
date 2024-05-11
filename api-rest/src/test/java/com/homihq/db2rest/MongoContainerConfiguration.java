package com.homihq.db2rest;

import com.homihq.db2test.mongo.multidb.RoutingMongoTemplate;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.restdocs.RestDocumentationExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@ExtendWith({RestDocumentationExtension.class})
public class MongoContainerConfiguration {
    @Container
    static final GenericContainer<?> mongodbContainer = new GenericContainer<>(
            DockerImageName.parse("mongo:4.0.10"))
            .withExposedPorts(27017)
            .withCopyFileToContainer(MountableFile.forClasspathResource("./mongo/mongo-sakila.js"),
                    "/docker-entrypoint-initdb.d/mongo-sakila.js");

   static  {
        mongodbContainer.start();
    }


    @Bean
    public RoutingMongoTemplate routingMongoTemplate() {
       String mongoUri = MongoContainerConfiguration.getUri();
       String databaseName = "SampleCollections";

        RoutingMongoTemplate routingMongoTemplate = new RoutingMongoTemplate();

        SimpleMongoClientDatabaseFactory simpleMongoClientDatabaseFactory =
                new SimpleMongoClientDatabaseFactory(
                        MongoClients.create(mongoUri), databaseName
                );

        MongoTemplate mongoTemplate = new MongoTemplate(simpleMongoClientDatabaseFactory);

        routingMongoTemplate.add("mongo", mongoTemplate);

        return routingMongoTemplate;
    }

    private static String getUri() {
        return "mongodb://admin:admin@" + mongodbContainer.getHost() + ":" + mongodbContainer.getFirstMappedPort()
                + "/SampleCollections";
    }
}
