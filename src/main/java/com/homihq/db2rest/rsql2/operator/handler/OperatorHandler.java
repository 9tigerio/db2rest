package com.homihq.db2rest.rsql2.operator.handler;

import com.homihq.db2rest.model.DbColumn;

import java.util.List;
import java.util.Map;

public interface OperatorHandler {

    String PREFIX = ":";

    String handle(DbColumn column, String value, Class type, Map<String, Object> paramMap);

    default String handle(DbColumn column, List<String> value, Class type, Map<String, Object> paramMap) {
        return handle(column, value.get(0), type, paramMap);
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
