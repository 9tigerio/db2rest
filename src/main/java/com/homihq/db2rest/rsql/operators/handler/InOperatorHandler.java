package com.homihq.db2rest.rsql.operators.handler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class InOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " in ";

    @Override
    public String handle(String columnName, String value, Class type) {
        return handle(columnName, Arrays.asList(value), type);
    }

    @Override
    public String handle(String columnName, List<String> values, Class type) {
        return columnName + " in (" +
            values.stream().map(value -> parseValue(value, type)).collect(Collectors.joining(",")) + ")";
    }
}
