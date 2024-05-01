package com.homihq.db2rest.jdbc.config.sql;

import com.homihq.db2rest.jdbc.config.model.DbTable;

import java.util.List;

public record DbMeta(String productName, int majorVersion, String driverName, String driverVersion, List<DbTable> dbTables) {
}
