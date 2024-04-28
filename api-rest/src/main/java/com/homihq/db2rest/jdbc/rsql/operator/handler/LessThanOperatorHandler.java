package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.jdbc.core.model.DbWhere;
import com.homihq.db2rest.jdbc.dialect.Dialect;
import com.homihq.db2rest.jdbc.core.model.DbColumn;

import java.util.Map;

public class LessThanOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " < ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {
        Object vo = dialect.processValue(value, type,null);

        if(dialect.supportAlias()) {

            String key = reviewAndSetParam(dialect.getAliasedNameParam(column, dbWhere.isDelete()), vo, paramMap);
            return dialect.getAliasedName(column, dbWhere.isDelete()) + OPERATOR + PREFIX + key;
        }
        else{
            String key = reviewAndSetParam(column.name(), vo, paramMap);
            return column.name() + OPERATOR + PREFIX + key;
        }
    }

}
