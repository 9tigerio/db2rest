package com.homihq.db2rest.rsql.operators.handler;

public class LikeOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " like ";

    @Override
    public String handle(String columnName, String value, Class type) {
        return columnName + OPERATOR + "'%" + value + "%'";
    }

}
