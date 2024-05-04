package com.homihq.db2test.mongo.repository;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.core.dto.UpdateResponse;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MongoRepository {

    private final MongoTemplate mongoTemplate;


    public CreateResponse save(String collectionName, List<String> includedFields,
                               Map<String, Object> data) {

        var document = new Document();
        data.forEach(document::append);
        var savedDocument = mongoTemplate.save(document, collectionName);
        return new CreateResponse(1, savedDocument.getObjectId("_id"));

    }

    public UpdateResponse patch(Query query, String collectionName, Map<String, Object> data) {
        var updateDefinition = new Update();
        data.forEach(updateDefinition::set);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, updateDefinition, collectionName);
        return new UpdateResponse((int) updateResult.getModifiedCount());
    }

    public DeleteResponse delete(Query query, String collectionName) {
        DeleteResult deleteResult = mongoTemplate.remove(query, collectionName);
        var rows = deleteResult.getDeletedCount();
        log.debug("Number of documents deleted - {}", rows);
        return DeleteResponse.builder().rows((int) rows).build();
    }

    public Object find(Query query, String collectionName) {
        return mongoTemplate.find(query, Object.class, collectionName);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> findOne(Query query, String collectionName) {
        return mongoTemplate.findOne(query, LinkedHashMap.class, collectionName);
    }

    public CountResponse count(Query query, String collectionName) {
        var count = mongoTemplate.count(query, collectionName);
        return new CountResponse(count);
    }
}
