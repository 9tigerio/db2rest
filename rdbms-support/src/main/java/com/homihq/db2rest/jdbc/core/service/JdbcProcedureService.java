package com.homihq.db2rest.jdbc.core.service;


import com.homihq.db2rest.core.exception.RpcException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class JdbcProcedureService implements ProcedureService {

    private final JdbcManager jdbcManager;

    @Override
    public Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams) {
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
        Dialect dialect = jdbcManager.getDialect(dbId);

        log.info("Dialect selected: {}", dialect);
        log.info("inParams: {}", inParams);

        return doExecuteInternal(jdbcTemplate, subRoutineName, inParams);
    }

    private Map<String, Object> doExecuteInternal(JdbcTemplate jdbcTemplate,
                                                  String subRoutineName, Map<String, Object> inParams) {
        jdbcTemplate.setResultsMapCaseInsensitive(true);

        try {
            return new SimpleJdbcCall(jdbcTemplate).withProcedureName(subRoutineName).execute(inParams);
        } catch (InvalidDataAccessApiUsageException ex) {
            throw new RpcException(subRoutineName, inParams);
        }
    }
}
