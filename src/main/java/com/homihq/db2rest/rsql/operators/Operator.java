package com.homihq.db2rest.rsql.operators;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import java.util.List;

public interface Operator {

    SqlCriterion handle(SqlColumn<Object> column, String value, Class<?> type);

    default SqlCriterion handle(SqlColumn<Object> column, List<String> value, Class<?> type) {
        return handle(column, value.get(0), type);
    }

    /*
    default Object parseValue(String value, Class<?> type) {

        if (String.class == type) {
            return value;
        }
        else if (Boolean.class == type || boolean.class == type) {
            return Boolean.valueOf(value);
        }
        else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(value);
        }

        else {
            return value;
        }

    }

     */

}
