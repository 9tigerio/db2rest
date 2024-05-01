package com.homihq.db2rest.jdbc.config.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JdbcFunctionService implements FunctionService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName) {
        return new SimpleJdbcCall(jdbcTemplate).withFunctionName(subRoutineName);
    }

    @Override
    public Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams) {
        return doExecute(jdbcTemplate, subRoutineName, inParams);
    }
}
