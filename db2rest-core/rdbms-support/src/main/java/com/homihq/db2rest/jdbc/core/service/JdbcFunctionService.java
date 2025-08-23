package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JdbcFunctionService implements FunctionService {

    private final JdbcManager jdbcManager;

    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String dbId, String subRoutineName) {

        JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
        return new SimpleJdbcCall(jdbcTemplate).withFunctionName(subRoutineName);
    }

    @Override
    @Transactional
    public Map<String, Object> execute(String dbId, String subRoutineName, Map<String, Object> inParams) {
        TransactionTemplate transactionTemplate = jdbcManager.getTxnTemplate(dbId);
        
        return transactionTemplate.execute(status -> {
            try {
                JdbcTemplate jdbcTemplate = jdbcManager.getNamedParameterJdbcTemplate(dbId).getJdbcTemplate();
                Map<String, Object> res = doExecute(jdbcTemplate, dbId, subRoutineName, inParams);
                
                return res;
            } catch (Exception e) {
                status.setRollbackOnly();
                log.error("Error executing function: {}", e.getMessage(), e);
                throw e;
            }
        });
    }
}
