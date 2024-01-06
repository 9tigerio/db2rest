package com.homihq.db2rest.rsql.v2.operators.handler;

import com.homihq.db2rest.rest.read.model.RCondition;
import com.homihq.db2rest.rsql.v2.operators.Operator;

import java.util.Arrays;
import java.util.List;


public class InOperator implements Operator {

    private static final String OPERATOR = " in ";

    @Override
    public RCondition handle(String columnName, String value, Class type) {
        return handle(columnName, Arrays.asList(value), type);
    }

    @Override
    public RCondition handle(String columnName, List<String> values, Class type) {
        Object [] v =
        values.stream().map(value -> parseValue(value, type)).toList().toArray();

        return RCondition.builder()
                .columnName(columnName)
                .operator(OPERATOR)
                .values(v)
                .build();
    }
}
