package com.homihq.db2rest.rest.meta.schema;

import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
public class TableWithColumnsObject extends TableObject {
    private final List<ColumnObject> columns;

    public TableWithColumnsObject(DbTable dbTable) {
        super(dbTable);
        this.columns = dbTable.dbColumns().stream().map(ColumnObject::new).toList();
    }
}
