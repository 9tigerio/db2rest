package com.homihq.db2rest.jdbc.core.service;


import java.util.Map;

public interface UpdateService {

    int patch(String dbId, String schemaName, String tableName, Map<String, Object> data, String filter);
}
