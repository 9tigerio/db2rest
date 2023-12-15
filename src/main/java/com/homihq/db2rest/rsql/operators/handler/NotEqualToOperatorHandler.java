package com.homihq.db2rest.rsql.operators.handler;

public class NotEqualToOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " != ";

    @Override
    public String handle(String columnName, String value, Class type) {
        return columnName + OPERATOR + parseValue(value, type);
    }

}
