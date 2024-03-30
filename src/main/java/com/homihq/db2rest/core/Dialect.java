package com.homihq.db2rest.core;

import com.homihq.db2rest.core.model.DbTable;


import java.util.List;
import java.util.Map;

public interface Dialect {

    default boolean supportBatchReturnKeys() {
        return true;
    }
    default boolean supportAlias() {
        return true;
    }

    void processTypes(DbTable table, List<String> insertableColumns, Map<String,Object> data);


    default Object processValue(String value, Class<?> type, String format) {
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
}
