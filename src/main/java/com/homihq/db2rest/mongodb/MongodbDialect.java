package com.homihq.db2rest.mongodb;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MongodbDialect implements Dialect {
    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        //TODO : To be implemented
    }
}
