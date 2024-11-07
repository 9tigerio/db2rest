package com.homihq.db2rest.jdbc.rest.meta.schema;

import com.homihq.db2rest.jdbc.config.model.DbColumn;

public record ColumnObject(String schema, Boolean pk, String columnDataTypeName) {
    public ColumnObject(DbColumn dbColumn) {
        this(
                dbColumn.name(),
                dbColumn.pk(),
                dbColumn.columnDataTypeName()
        );
    }
}

