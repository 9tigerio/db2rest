package com.homihq.db2rest.jdbc.core.service;


public interface DeleteService {

    int delete(
            String dbName,
            String schemaName, String tableName, String filter);
}
