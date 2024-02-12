package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import java.util.Map;

public class GreaterThanEqualToOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " >= ";

    @Override
    public String handle(String columnName, String value, Class type, Map<String, Object> paramMap) {

        Object vo = parseValue(value, type);

        paramMap.put(columnName, vo);

        return columnName + OPERATOR + PREFIX + columnName;
    }

}
