package com.homihq.db2rest.jdbc.core.service;

import org.springframework.transaction.annotation.Transactional;

public interface DeleteService {
    @Transactional
    int delete(String schemaName, String tableName, String filter);
}
