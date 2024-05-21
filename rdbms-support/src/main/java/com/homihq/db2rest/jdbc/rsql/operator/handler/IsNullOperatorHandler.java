package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbWhere;

import java.util.Map;

public class IsNullOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " is null ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {
        //Object vo = dialect.processValue(value, type, null);

        if(dialect.supportAlias()) {
            //String key = reviewAndSetParam(dialect.getAliasedNameParam(column, dbWhere.isDelete()), null, paramMap);
            return dialect.getAliasedName(column, dbWhere.isDelete()) + OPERATOR ;
        }
        else{
            //String key = reviewAndSetParam(column.name(), null, paramMap);
            return column.name() + OPERATOR ;
        }
    }

}
