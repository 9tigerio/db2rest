package com.homihq.db2rest.rsql2.operator.handler;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;

import java.util.Map;

public class GreaterThanOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " > ";

    @Override
    public String handle(Dialect dialect, DbColumn column, String value, Class type, Map<String, Object> paramMap) {
        Object vo = parseValue(value, type);

        paramMap.put(column.getAliasedNameParam(), vo);

        return column.getAliasedName() + OPERATOR + PREFIX + column.getAliasedNameParam();
    }

}
