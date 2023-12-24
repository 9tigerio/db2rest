package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.model.JoinTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.field;



@Component
@Slf4j
public class SelectBuilder {

    public List<Field<?>> build(Table<?> table, List<String> columnNames, Table<?> jTable, JoinTable jt) {

        List<Field<?>> fields = new ArrayList<>();

        addFieldsByTable(table, columnNames, fields);

        if(Objects.nonNull(jt.columns()) &&
                !jt.columns().isEmpty()) {
            addFieldsByTable(jTable, jt.columns(), fields);
        }

        return fields;
    }

    private void addFieldsByTable(Table<?> table, List<String> columnNames, List<Field<?>> fields) {

        for(String columnName : columnNames) {
            String [] parts = columnName.split(":");
            String alias;
            String colName;
            if(parts.length > 1) {
                colName = parts[0];
                alias = parts[1];

            }
            else{
                colName = parts[0];
                alias = "";
            }

            Field<?> f =
            Arrays.stream(table.fields()).filter(field -> StringUtils.equalsIgnoreCase(colName, field.getName()))
                    .findFirst().orElseThrow(() -> new RuntimeException("Column not found"));

            if(StringUtils.isNotBlank(alias)) {

                fields.add(field(f.getQualifiedName(), f.getType()).as(alias));
            }
            else{
                fields.add(field(f.getQualifiedName(), f.getType()));
            }

        }
    }


}
