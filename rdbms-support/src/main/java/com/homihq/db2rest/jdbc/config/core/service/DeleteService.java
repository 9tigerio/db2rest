package com.homihq.db2rest.jdbc.config.core.service;

import org.springframework.transaction.annotation.Transactional;

public interface DeleteService {
    @Transactional
    int delete(String schemaName, String tableName, String filter);
}
