package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.dbop.DbOperationService;
import com.homihq.db2rest.rest.read.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomQueryService {
    private final DbOperationService dbOperationService;

    Object find(QueryRequest queryRequest) {
        return dbOperationService.queryCustom(queryRequest.single(),
                queryRequest.sql(),
                queryRequest.params());
    }

}
