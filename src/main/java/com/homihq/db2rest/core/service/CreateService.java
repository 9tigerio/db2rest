package com.homihq.db2rest.core.service;

import com.homihq.db2rest.rest.create.dto.CreateResponse;
import java.util.List;
import java.util.Map;

public interface CreateService {

    CreateResponse save(String schemaName, String tableName, List<String> includedColumns,
                        Map<String, Object> data, boolean tsIdEnabled, List<String> sequences);
}
