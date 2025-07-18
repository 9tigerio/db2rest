package com.homihq.db2rest.rest.meta.schema;

import com.homihq.db2rest.jdbc.config.model.DbColumn;

public record ColumnObject(String name, Boolean pk, String dataType) {
    public ColumnObject(DbColumn dbColumn) {
        this(
                dbColumn.name(),
                dbColumn.pk(),
                dbColumn.columnDataTypeName()
        );
    }
}

