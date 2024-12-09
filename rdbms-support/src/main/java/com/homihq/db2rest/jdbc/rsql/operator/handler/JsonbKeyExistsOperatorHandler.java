package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class JsonbKeyExistsOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " is not null ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class<?> type, Map<String, Object> paramMap) {
        if (dialect.supportAlias()) {
            return dialect.getAliasedName(column, dbWhere.isDelete()) + column.jsonParts()
                    + OPERATOR;
        } else {
            return column.name() + column.jsonParts() + OPERATOR;
        }

    }

}
