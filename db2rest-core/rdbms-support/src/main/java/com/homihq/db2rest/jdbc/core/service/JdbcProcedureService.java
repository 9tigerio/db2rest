package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.core.exception.RpcException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class JdbcProcedureService implements ProcedureService {

    private final JdbcManager jdbcManager;

    @Override
    public Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams
    ,List<String> resultSetKeys) {
        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
        Dialect dialect = jdbcManager.getDialect(dbId);

        log.debug("Dialect selected: {}", dialect);
        log.debug("inParams: {}", inParams);

        return doExecuteInternal(jdbcTemplate, subRoutineName, inParams, resultSetKeys);
    }

    private Map<String, Object> doExecuteInternal(JdbcTemplate jdbcTemplate,
                                                  String subRoutineName, Map<String, Object> inParams
            ,List<String> resultSetKeys) {
        jdbcTemplate.setResultsMapCaseInsensitive(true);

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(subRoutineName);

        if (Objects.nonNull(resultSetKeys) && !resultSetKeys.isEmpty()) {

            for (String key : resultSetKeys) {
                simpleJdbcCall.returningResultSet(key, new ColumnMapRowMapper());
            }
        }

        try {
            return simpleJdbcCall.execute(inParams);
        } catch (InvalidDataAccessApiUsageException ex) {
            ex.printStackTrace();
            throw new RpcException(subRoutineName, inParams);
        }
    }
}
