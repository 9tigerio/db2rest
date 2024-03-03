package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.service.CustomQueryService;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.rest.read.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
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
