package com.homihq.db2rest.dialect;

import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.Map;

public interface Dialect {

    boolean canSupport(DatabaseInfo databaseInfo);

    void processTypes(DbTable table, List<String> insertableColumns, Map<String,Object> data);


    default Object processValue(String value, Class<?> type, String format) {
        if (String.class == type) {
            //return "'" + value + "'";

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
