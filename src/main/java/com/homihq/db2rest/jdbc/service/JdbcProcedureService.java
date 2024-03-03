package com.homihq.db2rest.jdbc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;


@Slf4j
public class JdbcProcedureService extends SubRoutine {

    public JdbcProcedureService(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName) {
        return new SimpleJdbcCall(jdbcTemplate).withProcedureName(subRoutineName);
    }
}
