package com.homihq.db2rest.jdbc.rsql.operator.handler;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.model.DbColumn;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


public interface OperatorHandler {

    String PREFIX = ":";

    String handle(Dialect dialect, DbColumn column, String value, Class type, Map<String, Object> paramMap);

    default String handle(Dialect dialect, DbColumn column, List<String> value, Class type, Map<String, Object> paramMap) {
        return handle(dialect,column, value.get(0), type, paramMap);
    }

    default Object parseValue(String value, Class type) {
        System.out.println("Type - "  + type);
        if (String.class == type) {
            return "'" + value + "'";
        }
        else if (Boolean.class == type || boolean.class == type) {
            Boolean aBoolean = Boolean.valueOf(value);
            return aBoolean ? "1" : "0";
        }
        else if (Short.class == type || short.class == type) {
            return Short.valueOf(value);

        }
        else {
            return value;
        }

    }

}
