package com.homihq.db2rest.rest.rpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;
@RequiredArgsConstructor
@Slf4j
public abstract class SubRoutine {

    final JdbcTemplate jdbcTemplate;

    Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams) {
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        SqlParameterSource in = new MapSqlParameterSource()
                .addValues(inParams);
        return getSimpleJdbcCall(subRoutineName).execute(in);
    }

    abstract SimpleJdbcCall getSimpleJdbcCall(String subRoutineName);
}
