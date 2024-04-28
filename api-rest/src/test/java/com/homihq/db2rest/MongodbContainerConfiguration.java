package com.homihq.db2rest;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Profile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@ExtendWith({RestDocumentationExtension.class})
public class MongodbContainerConfiguration {
    @Container
    static final GenericContainer<?> mongodbContainer = new GenericContainer<>(
            DockerImageName.parse("mongo:4.0.10"))
            .withExposedPorts(27017)
            .withCopyFileToContainer(MountableFile.forClasspathResource("./mongo/mongo-sakila.js"),
                    "/docker-entrypoint-initdb.d/mongo-sakila.js");

   static  {
        mongodbContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MongodbContainerConfiguration::getUri);
        registry.add("spring.data.mongodb.database", () -> "SampleCollections");
    }

    private static String getUri() {
        return "mongodb://admin:admin@" + mongodbContainer.getHost() + ":" + mongodbContainer.getFirstMappedPort()
                + "/SampleCollections";
    }
}
