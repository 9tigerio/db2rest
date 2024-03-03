package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbColumn;

import java.util.List;
import java.util.Map;

public interface OperatorHandler {

    String PREFIX = ":";

    String handle(Dialect dialect, DbColumn column, String value, Class type, Map<String, Object> paramMap);

    default String handle(Dialect dialect, DbColumn column, List<String> value, Class type, Map<String, Object> paramMap) {
        return handle(dialect,column, value.get(0), type, paramMap);
    }

    default String parseValue(String value, Class type) {

        if (String.class == type) {
            return "'" + value + "'";
        }
        else if (Boolean.class == type || boolean.class == type) {
            Boolean aBoolean = Boolean.valueOf(value);
            return aBoolean ? "1" : "0";
        }
        else {
            return value;
        }

    }

}
