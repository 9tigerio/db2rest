package com.homihq.db2rest.mongodb.service;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.CreateService;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MongodbCreateService implements CreateService {
    private final DbOperationService dbOperationService;

    private final Dialect dialect;

    @Override
    public CreateResponse save(String schemaName, String collectionName, List<String> includedColumns,
                               Map<String, Object> data, boolean tsIdEnabled, List<String> sequences) {
        return dbOperationService.create(data, collectionName);
    }
}
