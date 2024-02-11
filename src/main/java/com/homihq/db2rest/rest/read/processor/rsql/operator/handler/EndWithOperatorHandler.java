package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import java.util.Map;

public class EndWithOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " like ";

    @Override
    public String handle(String columnName, String value, Class type, Map<String, Object> paramMap) {
        return columnName + OPERATOR + "'%" + value + "'";
    }

}
