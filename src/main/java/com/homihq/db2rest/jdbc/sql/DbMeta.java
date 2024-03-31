package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.model.DbTable;

import java.util.List;

public record DbMeta(String productName, int majorVersion, String driverName, String driverVersion, List<DbTable> dbTables) {
}
