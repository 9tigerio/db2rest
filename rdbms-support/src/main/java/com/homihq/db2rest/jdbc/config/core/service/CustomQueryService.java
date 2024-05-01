package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.jdbc.config.dto.QueryRequest;

public interface CustomQueryService {
    Object find(QueryRequest queryRequest);
}
