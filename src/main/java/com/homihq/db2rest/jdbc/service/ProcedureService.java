package com.homihq.db2rest.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "jdbc")
public class ProcedureService extends SubRoutine {

    public ProcedureService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName) {
        return new SimpleJdbcCall(jdbcTemplate).withProcedureName(subRoutineName);
    }
}
