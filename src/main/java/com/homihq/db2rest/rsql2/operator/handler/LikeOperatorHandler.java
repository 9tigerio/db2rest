package com.homihq.db2rest.rsql2.operator.handler;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;

import java.util.Map;

public class LikeOperatorHandler implements OperatorHandler {

   private static final String OPERATOR = " like ";

    @Override
    public String handle(Dialect dialect, DbColumn columnName, String value, Class type, Map<String, Object> paramMap) {
        return columnName + OPERATOR + "'%" + value + "%'";
    }

}
