package com.homihq.db2rest.rsql2.operator.handler;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NotInOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " not in ";

    @Override
    public String handle(Dialect dialect, DbColumn columnName, String value, Class type, Map<String, Object> paramMap) {
        return handle(dialect, columnName, Arrays.asList(value), type, paramMap);
    }

    @Override
    public String handle(Dialect dialect, DbColumn columnName, List<String> values, Class type, Map<String, Object> paramMap) {
        return columnName + " not in (" +
            values.stream().map(value -> parseValue(value, type)).collect(Collectors.joining(",")) + ")";
    }

}
