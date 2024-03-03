package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.exception.RpcException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;
@RequiredArgsConstructor
@Slf4j
public abstract class SubRoutine {

    final JdbcTemplate jdbcTemplate;

    public Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams) {
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        SqlParameterSource in = new MapSqlParameterSource()
                .addValues(inParams);
        try {
            return getSimpleJdbcCall(subRoutineName).execute(in);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new RpcException(subRoutineName, inParams);
        }
    }

    abstract public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName);
}
