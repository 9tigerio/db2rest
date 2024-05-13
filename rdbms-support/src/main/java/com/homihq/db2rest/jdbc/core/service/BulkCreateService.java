package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.dto.CreateBulkResponse;


import java.util.List;
import java.util.Map;

public interface BulkCreateService {
    CreateBulkResponse saveBulk(
            String dbName,
            String schemaName, String tableName,
                                List<String> includedColumns,
                                List<Map<String, Object>> dataList,
                                boolean tsIdEnabled, List<String> sequences);
}
