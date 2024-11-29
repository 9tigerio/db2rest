package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.exception.RpcException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;


public interface SubRoutine {

    default Map<String, Object> doExecute(JdbcTemplate jdbcTemplate,
                                          String dbId,
                                          String subRoutineName, Map<String, Object> inParams) {
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        SqlParameterSource in = new MapSqlParameterSource()
                .addValues(inParams);
        try {
            return getSimpleJdbcCall(dbId, subRoutineName).execute(in);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new RpcException(subRoutineName, inParams);
        }
    }

    SimpleJdbcCall getSimpleJdbcCall(String dbId, String subRoutineName);
}
