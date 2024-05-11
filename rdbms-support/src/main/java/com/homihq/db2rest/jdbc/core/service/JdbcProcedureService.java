package com.homihq.db2rest.jdbc.core.service;


import com.homihq.db2rest.jdbc.JdbcManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class JdbcProcedureService implements ProcedureService {

    private final JdbcManager jdbcManager;

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String dbName,String subRoutineName) {
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbName).getJdbcTemplate();
        return new SimpleJdbcCall(jdbcTemplate).withProcedureName(subRoutineName);
    }

    @Override
    public Map<String, Object> execute(String dbName, String subRoutineName, Map<String, Object> inParams) {
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbName).getJdbcTemplate();
        return doExecute(jdbcTemplate, dbName, subRoutineName, inParams);
    }
}
