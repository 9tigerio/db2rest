package com.homihq.db2rest.jdbc.core.service;


import com.homihq.db2rest.core.exception.RpcException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service
public class JdbcProcedureService implements ProcedureService {

    private final JdbcManager jdbcManager;

    @Override
    public Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams) {
        TransactionTemplate transactionTemplate = jdbcManager.getTxnTemplate(dbId);
        
        return transactionTemplate.execute(status -> {
            try {
                JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
                Dialect dialect = jdbcManager.getDialect(dbId);

                log.debug("Dialect selected: {}", dialect);
                log.debug("inParams: {}", inParams);

                Map<String, Object> res = doExecuteInternal(jdbcTemplate, subRoutineName, inParams);
                
                return res;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("Error executing procedure: {}", e.getMessage(), e);
                throw e;
            }
        });
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
