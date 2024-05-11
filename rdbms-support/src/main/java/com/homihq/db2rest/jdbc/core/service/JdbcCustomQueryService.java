package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class JdbcCustomQueryService implements CustomQueryService {
    private final JdbcManager jdbcManager;
    private final DbOperationService dbOperationService;

    @Override
    public Object find(String dbName, QueryRequest queryRequest) {
        return dbOperationService.queryCustom(
                jdbcManager.getNamedParameterJdbcTemplate(dbName),
                queryRequest.single(),
                queryRequest.sql(),
                queryRequest.params());
    }

}
