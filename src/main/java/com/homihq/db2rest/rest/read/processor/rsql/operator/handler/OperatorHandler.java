package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;

import java.util.List;
import java.util.Map;

public interface OperatorHandler {

    String PREFIX = ":";

    String handle(String columnName, String value, Class type, Map<String, Object> paramMap);

    default String handle(String columnName, List<String> value, Class type, Map<String, Object> paramMap) {
        return handle(columnName, value.get(0), type, paramMap);
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
