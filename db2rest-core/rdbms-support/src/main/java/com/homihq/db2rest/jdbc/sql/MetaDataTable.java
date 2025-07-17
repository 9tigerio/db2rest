package com.homihq.db2rest.jdbc.sql;

public record MetaDataTable(
        String tableName,
        String catalog,
        String schema,
        String tableType,
        String tableAlias
) {
}
