package com.homihq.db2rest.rsql.operators;

import org.jooq.Condition;

import java.util.List;

public interface OperatorHandler {

    Condition handle(String columnName, String value, Class type);

    default Condition handle(String columnName, List<String> value, Class type) {
        return handle(columnName, value.get(0), type);
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
