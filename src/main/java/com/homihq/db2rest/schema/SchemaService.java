package com.homihq.db2rest.schema;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.jooq.Record;
import org.springframework.stereotype.Component;
import java.util.*;


@Component
@Slf4j
@RequiredArgsConstructor
public final class SchemaService {


    private final DSLContext dslContext;
    private final Db2RestConfigProperties db2RestConfigProperties;

    private List<Table<?>> tables;

    public Optional<Table<?>> getTableByName(String name) {
        return tables.stream().filter( t ->
                name.equals(t.getName())
        ).findFirst();
    }

    public Optional<Table<?>> getTableByNameAndSchema(String schemaName , String tableName) {

        return tables.stream()
                .filter( t ->
                        StringUtils.equalsIgnoreCase(tableName, t.getName())
                 && StringUtils.equalsIgnoreCase(schemaName, t.getSchema().getName())
        ).findFirst();
    }


    @PostConstruct
    public void load() {
        //TODO add a property to cache or not as this can be lot of data in memory , on demand caching
        //may be better
        String [] schemas = this.db2RestConfigProperties.getSchemas();

         tables = dslContext.meta().getTables().stream()
                 .filter(t -> StringUtils.equalsAnyIgnoreCase(
                         t.getSchema().getName(), schemas))
                 .toList();

         log.info("Tables ->> {}", tables);
    }

    public Condition createJoin2(Table<?> table, Table<?> jTable) {

        List<Fk> fkList = new ArrayList<>();

        for(ForeignKey<?, ?> fk : table.getReferencesTo(jTable)) {
            List<FkFields> fieldList = new ArrayList<>();
            for (Field<?> fkField : fk.getFields()) {
                FkFields field = new FkFields();
                field.setFkField((Field<Record>) fkField);
                fieldList.add(field);
            }

            int counter = 0;
            for (Field<?> fkField : fk.getKey().getFields()) {
                FkFields field = fieldList.get(counter);
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
            for(FkFields fkFields : fk.fieldList) {
                if(Objects.isNull(condition)) {
                    condition = fkFields.getCondition();
                }
                else{
                    condition.and(fkFields.getCondition());
                }
            }
        }

        return condition;
    }



}
