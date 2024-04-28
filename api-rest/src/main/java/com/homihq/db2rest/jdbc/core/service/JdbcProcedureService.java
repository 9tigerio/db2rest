package com.homihq.db2rest.jdbc.core.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class JdbcProcedureService implements ProcedureService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName) {
        return new SimpleJdbcCall(jdbcTemplate).withProcedureName(subRoutineName);
    }

    @Override
    public Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams) {
        return doExecute(jdbcTemplate, subRoutineName, inParams);
    }
}
