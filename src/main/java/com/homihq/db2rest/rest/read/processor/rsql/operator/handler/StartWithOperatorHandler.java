package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import com.homihq.db2rest.rest.read.model.DbColumn;

import java.util.Map;

public class StartWithOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " like ";

    @Override
    public String handle(DbColumn columnName, String value, Class type, Map<String, Object> paramMap) {
        return columnName + OPERATOR + "'" + value + "%'";
    }

}
