package com.homihq.db2test.mongo.multidb;

import com.homihq.db2rest.multidb.DatabaseContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoutingMongoTemplate {

    private final Map<String, MongoTemplate> mongoTemplateMap = new ConcurrentHashMap<>();

    public void add(String dbName, MongoTemplate mongoTemplate) {
        this.mongoTemplateMap.put(dbName, mongoTemplate);
    }

    public MongoTemplate get() {
        final String dbName = DatabaseContextHolder.getCurrentDbId();

        log.info("Resolved mongodb - {}", dbName);

        return this.mongoTemplateMap.get(dbName);
    }
}
