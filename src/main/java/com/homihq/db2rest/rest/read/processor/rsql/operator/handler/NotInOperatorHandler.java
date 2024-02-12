package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import com.homihq.db2rest.rest.read.model.DbColumn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotInOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " not in ";

    @Override
    public String handle(DbColumn columnName, String value, Class type, Map<String, Object> paramMap) {
        return handle(columnName, Arrays.asList(value), type, paramMap);
    }

    @Override
    public String handle(DbColumn columnName, List<String> values, Class type, Map<String, Object> paramMap) {
        return columnName + " not in (" +
            values.stream().map(value -> parseValue(value, type)).collect(Collectors.joining(",")) + ")";
    }

}
