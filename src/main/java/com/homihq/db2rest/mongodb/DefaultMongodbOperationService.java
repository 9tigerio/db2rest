package com.homihq.db2rest.mongodb;

import com.homihq.db2rest.rest.create.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DefaultMongodbOperationService implements MongodbOperationService {

    private final MongoTemplate mongoTemplate;

    @Override
    public CreateResponse create(Map<String, Object> data, String collectionName) {
        var document = new Document();
        data.forEach(document::append);
        var savedDocument = mongoTemplate.save(document, collectionName);
        return new CreateResponse(1, savedDocument.getObjectId("_id"));
    }
}
