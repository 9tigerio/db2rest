package com.homihq.db2rest.jdbc.config.sql;

public record MetaDataTable(String tableName, String catalog, String schema, String tableType, String tableAlias) {
}
