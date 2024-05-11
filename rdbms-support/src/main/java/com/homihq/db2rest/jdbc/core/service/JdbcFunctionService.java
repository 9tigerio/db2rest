package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JdbcFunctionService implements FunctionService {

    private final JdbcManager jdbcManager;

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String dbName, String subRoutineName) {
        log.info("dbName - {}", dbName);
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbName).getJdbcTemplate();
        return new SimpleJdbcCall(jdbcTemplate).withFunctionName(subRoutineName);
    }

    @Override
    public Map<String, Object> execute(String dbName, String subRoutineName, Map<String, Object> inParams) {
        log.info("dbName - {}", dbName);
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbName).getJdbcTemplate();
        return doExecute(jdbcTemplate, dbName, subRoutineName, inParams);
    }
}
