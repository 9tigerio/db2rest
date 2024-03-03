package com.homihq.db2rest.d1.service;

import com.homihq.db2rest.core.service.FunctionService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

public class D1FunctionService implements FunctionService {
    @Override
    public SimpleJdbcCall getSimpleJdbcCall(String subRoutineName) {
        throw new GenericDataAccessException("Invalid operation.Not implemented");
    }

    @Override
    public Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams) {
        throw new GenericDataAccessException("Invalid operation.Not implemented");
    }
}
