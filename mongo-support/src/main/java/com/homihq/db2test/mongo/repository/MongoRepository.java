package com.homihq.db2test.mongo.repository;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.core.dto.UpdateResponse;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class MongoRepository {

    private final MongoTemplate mongoTemplate;

    public CreateResponse save(String collectionName, List<String> includedFields,
                               Map<String, Object> data) {
        Map<String, Object> insertableData;
        if (!isEmpty(includedFields)) {
            Set<String> subsetKeys = new HashSet<>(includedFields);
            insertableData = filterMapBySubsetKeys(data, subsetKeys);
        } else {
            insertableData = data;
        }
        var insertableDocument = convertToDocument(insertableData);
        var savedDocument = mongoTemplate.save(insertableDocument, collectionName);
        return new CreateResponse(1, savedDocument.getObjectId("_id"));

    }

    public CreateBulkResponse saveAll(String collectionName, List<String> includedFields,
                                      List<Map<String, Object>> dataList) {
        List<Map<String, Object>> insertableDataList;
        if (!isEmpty(includedFields)) {
            Set<String> subsetKeys = new HashSet<>(includedFields);
            insertableDataList = dataList.stream()
                    .map(data -> filterMapBySubsetKeys(data, subsetKeys))
                    .toList();
        } else {
            insertableDataList = dataList;
        }
        List<Document> insertableDocumentList = insertableDataList.stream()
                .map(this::convertToDocument)
                .toList();
        Collection<Document> savedDocuments = mongoTemplate.insert(insertableDocumentList, collectionName);
        List<Integer> rows = Collections.nCopies(savedDocuments.size(), 1);
        List<ObjectId> keys = savedDocuments.stream().map(doc -> doc.getObjectId("_id")).toList();
        return new CreateBulkResponse(rows.stream()
                .mapToInt(Integer::intValue)
                .toArray(), keys);
    }

    private Map<String, Object> filterMapBySubsetKeys(Map<String, Object> source, Set<String> subsetKeys) {
        return source.entrySet().stream()
                .filter(entry -> subsetKeys.contains(entry.getKey()))
                .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Document convertToDocument(Map<String, Object> data) {
        var document = new Document();
        data.forEach(document::append);
        return document;
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

    public ExistsResponse exists(Query query, String collectionName) {
        return new ExistsResponse(mongoTemplate.exists(query, collectionName));
    }
}
