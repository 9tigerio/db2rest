package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class IsNullOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " is null ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class<?> type, Map<String, Object> paramMap) {
        return dialect.supportAlias()
                ? dialect.getAliasedName(column, dbWhere.isDelete()) + OPERATOR
                : column.name() + OPERATOR;
    }

}
