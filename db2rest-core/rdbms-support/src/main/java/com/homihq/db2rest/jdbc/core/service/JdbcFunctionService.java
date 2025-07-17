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
    public SimpleJdbcCall getSimpleJdbcCall(String dbId, String subRoutineName) {

        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
        return new SimpleJdbcCall(jdbcTemplate).withFunctionName(subRoutineName);
    }

    @Override
    public Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams) {

        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
        return doExecute(jdbcTemplate, dbId, subRoutineName, inParams);
    }
}
