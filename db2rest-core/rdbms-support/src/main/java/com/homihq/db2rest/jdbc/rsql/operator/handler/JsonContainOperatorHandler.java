package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class JsonContainOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " <@ ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {

        Object vo = dialect.processValue(value, type, null, column.columnDataTypeName());

        if (dialect.supportAlias()) {
            String key =
                    reviewAndSetParam(dialect.getAliasedNameParam(column, dbWhere.isDelete()), vo, paramMap);
            return PREFIX + key + "::jsonb" + OPERATOR
                    + dialect.getAliasedName(column, dbWhere.isDelete());
        } else {
            String key = reviewAndSetParam(column.name(), vo, paramMap);
            return PREFIX + key + "::jsonb" + OPERATOR + column.name();
        }

    }

}
