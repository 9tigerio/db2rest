package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.model.DbColumn;

import java.util.Map;

public class StartWithOperatorHandler implements OperatorHandler {

    private static final String OPERATOR = " like ";

    @Override
    public String handle(Dialect dialect, DbColumn column, String value, Class type, Map<String, Object> paramMap) {

        //Always a string
        Object vo = value + "%";

        if(dialect.supportAlias()) {

            String key = reviewAndSetParam(column.getAliasedNameParam(), vo, paramMap);
            return column.getAliasedName() + OPERATOR + PREFIX + key;
        }
        else{
            String key = reviewAndSetParam(column.name(), vo, paramMap);
            return column.name() + OPERATOR + PREFIX + key ;
        }
    }

}
