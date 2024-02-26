package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.dbop.JdbcOperationService;
import com.homihq.db2rest.rest.read.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomQueryService {
    private final JdbcOperationService jdbcOperationService;

    Object find(QueryRequest queryRequest) {
        return jdbcOperationService.queryCustom(queryRequest.single(),
                queryRequest.sql(),
                queryRequest.params());
    }

}
