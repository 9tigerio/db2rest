package com.homihq.db2rest.jdbc.rsql.operator;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbWhere;

import java.util.List;
import java.util.Map;
import java.util.Random;


public interface OperatorHandler {

    String PREFIX = ":";
    Random RANDOM = new Random();

    String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, String value, Class<?> type, Map<String, Object> paramMap);

    default String reviewAndSetParam(String key, Object value, Map<String, Object> paramMap) {
        if (paramMap.containsKey(key)) {
            String newKey = key + "_" + RANDOM.nextInt(20);
            paramMap.put(newKey, value);

            return newKey;
        } else {
            paramMap.put(key, value);
            return key;
        }
    }

    default String handle(Dialect dialect, DbColumn column, DbWhere dbWhere, List<String> value, Class<?> type, Map<String, Object> paramMap) {
        return handle(dialect, column, dbWhere, value.get(0), type, paramMap);
    }



}
