package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface BulkCreateService {
    @Transactional
    CreateBulkResponse saveBulk(String schemaName, String tableName,
                                List<String> includedColumns,
                                List<Map<String, Object>> dataList,
                                boolean tsIdEnabled);
}
