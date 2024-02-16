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

    Object processValue(String value, Class<?> type, String format);
}
