package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;

import java.util.Map;

public class EqualToOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " = ";

    @Override
    public String handle(Dialect dialect, DbColumn column, String value, Class type, Map<String, Object> paramMap) {

        Object vo = dialect.processValue(value, type, null);

        if(dialect.supportAlias()) {

            paramMap.put(column.getAliasedNameParam(), vo);
            return column.getAliasedName() + OPERATOR + PREFIX + column.getAliasedNameParam();
        }
        else{
            paramMap.put(column.name(), vo);
            return column.name() + OPERATOR + PREFIX + column.name();
        }

    }

}
