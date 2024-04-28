package com.homihq.db2rest.mongo.dialect;

import com.homihq.db2rest.jdbc.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MongoDialect {

    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        //TODO : To be implemented
    }
}
