package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.dto.QueryRequest;

public interface CustomQueryService {
    Object find(String dbId, QueryRequest queryRequest);
}
