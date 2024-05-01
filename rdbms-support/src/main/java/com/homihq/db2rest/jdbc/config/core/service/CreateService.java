package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.core.dto.CreateResponse;
import java.util.List;
import java.util.Map;

public interface CreateService {

    CreateResponse save(String schemaName, String tableName, List<String> includedColumns,
                        Map<String, Object> data, boolean tsIdEnabled, List<String> sequences);
}
