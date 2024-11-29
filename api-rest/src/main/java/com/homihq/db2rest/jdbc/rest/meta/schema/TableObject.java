package com.homihq.db2rest.jdbc.rest.meta.schema;

import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class TableObject {
    private final String schema;
    private final String name;
    private final String type;

    public TableObject(DbTable dbTable) {
        this.schema = dbTable.schema();
        this.name = dbTable.name();
        this.type = dbTable.type();
    }

}
