package com.homihq.db2test.mongo.multidb;

import com.homihq.db2rest.multidb.DatabaseContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RoutingMongoTemplate {

    private final Map<String, MongoTemplate> mongoTemplateMap = new ConcurrentHashMap<>();

    public void add(String dbId, MongoTemplate mongoTemplate) {
        this.mongoTemplateMap.put(dbId, mongoTemplate);
    }

    public MongoTemplate get() {
        final String dbId = DatabaseContextHolder.getCurrentDbId();

        log.info("Resolved mongodb - {}", dbId);

        return this.mongoTemplateMap.get(dbId);
    }
}
