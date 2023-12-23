package com.homihq.db2rest.schema;

import lombok.Data;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;


@Data
public class FkFields {

    Field<Record> fkField;
    Field<Record> fkRefField;

    public Condition getCondition() {
        return
                fkRefField.eq(fkField);
    }
}
