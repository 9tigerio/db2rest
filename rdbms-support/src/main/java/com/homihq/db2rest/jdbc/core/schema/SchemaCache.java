package com.homihq.db2rest.jdbc.core.schema;

import com.homihq.db2rest.jdbc.config.model.DbTable;


public interface SchemaCache {
    DbTable getTable(String tableName);



}
