package com.homihq.db2rest.mongo.repository;

import com.homihq.db2rest.mongo.dialect.MongoDialect;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MongoRepository {

    private final MongoTemplate mongoTemplate;
    private final MongoDialect dialect;


    public CreateResponse save(String collectionName, List<String> includedFields,
                               Map<String, Object> data) {

        var document = new Document();
        data.forEach(document::append);
        var savedDocument = mongoTemplate.save(document, collectionName);
        return new CreateResponse(1, savedDocument.getObjectId("_id"));

    }
}
