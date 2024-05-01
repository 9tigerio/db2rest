package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.jdbc.config.core.DbOperationService;
import com.homihq.db2rest.jdbc.config.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class JdbcCustomQueryService implements CustomQueryService {

    private final DbOperationService dbOperationService;

    @Override
    public Object find(QueryRequest queryRequest) {
        return dbOperationService.queryCustom(queryRequest.single(),
                queryRequest.sql(),
                queryRequest.params());
    }

}
