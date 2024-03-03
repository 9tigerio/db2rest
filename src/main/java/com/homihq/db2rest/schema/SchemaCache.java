package com.homihq.db2rest.schema;

import com.homihq.db2rest.core.model.DbTable;


public interface SchemaCache {
    DbTable getTable(String tableName);



}
