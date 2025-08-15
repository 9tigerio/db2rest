package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.operator.OperatorHandler;

import java.util.Map;

public class JsonbKeyExistsOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " is not null ";

    @Override
    public String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class type, Map<String, Object> paramMap) {

        //Object vo = dialect.processValue(value, type, null);

        if (dialect.supportAlias()) {
            // String key = reviewAndSetParam(dialect.getAliasedNameParam(column, dbWhere.isDelete()), vo, paramMap);
            return dialect.getAliasedName(column, dbWhere.isDelete()) + column.jsonParts()
                    + OPERATOR;
        } else {
            //String key = reviewAndSetParam(column.name(), vo, paramMap);
            return column.name() + column.jsonParts() + OPERATOR;
        }

    }

}
