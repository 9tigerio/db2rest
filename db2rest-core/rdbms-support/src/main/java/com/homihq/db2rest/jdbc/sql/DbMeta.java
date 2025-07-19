package com.homihq.db2rest.jdbc.sql;

import com.db2rest.jdbc.dialect.model.DbTable;

import java.util.List;

public record DbMeta(
        String productName,
        int majorVersion,
        String driverName,
        String driverVersion,
        List<DbTable> dbTables
) {
}
