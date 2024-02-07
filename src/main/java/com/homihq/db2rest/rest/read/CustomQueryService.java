package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.QueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomQueryService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    Object find(QueryRequest queryRequest) {
        return queryRequest.single() ?
                namedParameterJdbcTemplate.queryForMap(queryRequest.sql(), queryRequest.params()) :
                namedParameterJdbcTemplate.queryForList(queryRequest.sql(), queryRequest.params());
    }
}
