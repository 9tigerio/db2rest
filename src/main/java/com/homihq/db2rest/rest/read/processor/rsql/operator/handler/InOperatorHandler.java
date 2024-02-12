package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " in ";

    @Override
    public String handle(String columnName, String value, Class type, Map<String, Object> paramMap) {
        return handle(columnName, Arrays.asList(value), type, paramMap);
    }

    @Override
    public String handle(String columnName, List<String> values, Class type, Map<String, Object> paramMap) {
        return columnName + " in (" +
            values.stream().map(value -> parseValue(value, type)).collect(Collectors.joining(",")) + ")";
    }
}
