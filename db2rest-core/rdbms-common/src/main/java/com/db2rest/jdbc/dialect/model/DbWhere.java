package com.db2rest.jdbc.dialect.model;

import org.apache.commons.lang3.Strings;

import java.util.List;
import java.util.Map;

public record DbWhere(
        String tableName,
        DbTable table,
        List<DbColumn> columns,
        Map<String, Object> paramMap,
        String op,
        List<DbTable> allTables
) {

    public boolean isDelete() {
        return Strings.CI.equalsAny(op, "delete");
    }
}
