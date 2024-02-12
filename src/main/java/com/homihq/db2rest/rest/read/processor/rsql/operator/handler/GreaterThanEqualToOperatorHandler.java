package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import com.homihq.db2rest.rest.read.model.DbColumn;

import java.util.Map;

public class GreaterThanEqualToOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " >= ";

    @Override
    public String handle(DbColumn column, String value, Class type, Map<String, Object> paramMap) {

        Object vo = parseValue(value, type);

        paramMap.put(column.getAliasedNameParam(), vo);

        return column.getAliasedName() + OPERATOR + PREFIX + column.getAliasedNameParam();
    }

}
