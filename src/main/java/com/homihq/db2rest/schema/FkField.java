package com.homihq.db2rest.schema;

import lombok.Data;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;


@Data
public class FkField {
    String columnName;
    String tableName;

    String referenceTableName;
    String referenceColumnName;

    Field<Record> fkField;
    Field<Record> fkRefField;

    public Condition getCondition() {
        return
                fkRefField.eq(fkField);
    }
}
