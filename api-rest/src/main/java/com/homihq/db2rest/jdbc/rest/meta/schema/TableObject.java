package com.homihq.db2rest.jdbc.rest.meta.schema;

import com.homihq.db2rest.jdbc.config.model.DbTable;

import java.util.List;

public record TableObject(String schema, String name, String type, List<ColumnObject> columns) {
    public TableObject(DbTable dbTable) {
        this(
                dbTable.schema(),
                dbTable.name(),
                dbTable.type(),
                dbTable.dbColumns().stream().map(ColumnObject::new).toList()
        );
    };
}
