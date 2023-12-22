package com.homihq.db2rest.schema;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
@Slf4j
@RequiredArgsConstructor
public final class SchemaService {


    private final DSLContext dslContext;

    private List<Table<?>> tables;

    public Optional<Table<?>> getTableByName(String name) {
        return tables.stream().filter( t ->
                name.equals(t.getName())
        ).findFirst();
    }


    @PostConstruct
    public void load() {
        //TODO add a property to cache or not as this can be lot of data in memory , on demand caching
        //may be better
         tables = dslContext.meta().getTables();
    }


    public Condition createJoin(String tableName,  String joinTable) {

        Table<?> table =
        getTableByName(tableName)
                .orElseThrow(() -> new RuntimeException("Table - " + tableName + " not found"));

        Table<?> jTable = getTableByName(joinTable)
                .orElseThrow(() -> new RuntimeException("Table - " + tableName + " not found"));

        List<Fk> fkList = new ArrayList<>();

        for(ForeignKey<?, ?> fk : table.getReferencesTo(jTable)) {
            List<FkField> fieldList = new ArrayList<>();
            for (Field<?> fkField : fk.getFields()) {
                FkField field = new FkField();
                field.setTableName(tableName);
                field.setColumnName(fkField.getName());
                field.setFkField((Field<Record>) fkField);
                fieldList.add(field);
            }

            int counter = 0;
            for (Field<?> fkField : fk.getKey().getFields()) {
                FkField field = fieldList.get(counter);
                field.setReferenceTableName(joinTable);
                field.setReferenceColumnName(fkField.getName());
                field.setFkRefField((Field<Record>) fkField);
                counter++;
            }

            Fk foreignKey = new Fk();
            foreignKey.setName(fk.getName());
            foreignKey.setFieldList(fieldList);
            fkList.add(foreignKey);
        }


        Condition condition = null;

        for(Fk fk : fkList) {
            for(FkField fkField : fk.fieldList) {
                if(Objects.isNull(condition)) {
                    condition = fkField.getCondition();
                }
                else{
                    condition.and(fkField.getCondition());
                }
            }
        }

        return condition;
    }


}
