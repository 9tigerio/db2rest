package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class JsonContainInArrayOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " ?? ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {
        if (dialect.supportAlias()) {
            return dialect.getAliasedName(column, dbWhere.isDelete()) + column.jsonParts()
                    + OPERATOR + "'" + value + "'";
        } else {
            return column.name() + column.jsonParts() + OPERATOR + "'" + value + "'";
        }
    }

}
