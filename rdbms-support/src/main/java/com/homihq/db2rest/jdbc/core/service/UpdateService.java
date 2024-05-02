package com.homihq.db2rest.jdbc.core.service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface UpdateService {
    @Transactional
    int patch(String schemaName, String tableName, Map<String, Object> data, String filter);
}
