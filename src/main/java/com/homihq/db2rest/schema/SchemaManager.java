package com.homihq.db2rest.schema;

import com.homihq.db2rest.core.model.DbTable;


public interface SchemaManager {
    DbTable getTable(String tableName);

    DbTable getOneTable(String schemaName, String tableName);

}
