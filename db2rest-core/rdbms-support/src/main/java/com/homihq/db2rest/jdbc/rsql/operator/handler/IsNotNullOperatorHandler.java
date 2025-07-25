package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class IsNotNullOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " is not null ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {

        if (dialect.supportAlias()) {
            return dialect.getAliasedName(column, dbWhere.isDelete()) + OPERATOR;
        } else {
            return column.name() + OPERATOR;
        }
    }

}
