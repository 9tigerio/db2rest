package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rest.query.helper.model.SelectColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jooq.impl.DSL.field;



@Component
@Slf4j
public class SelectBuilder {

    public List<Field<?>> build(String schemaName, Table<?> table, String tableName , List<String> columnNames) {

        List<Field<?>> fields = new ArrayList<>();

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

            log.info("Col Name - {}. Alias - {}", colName, alias);
            Field<?> f =
            Arrays.stream(table.fields()).filter(field -> StringUtils.equalsIgnoreCase(colName, field.getName()))
                    .findFirst().orElseThrow(() -> new RuntimeException("Column not found"));


            if(StringUtils.isNotBlank(alias)) {
                log.info("Adding alias - {}", alias);

                log.info("field - {}", field(f.getQualifiedName(), f.getType()).as(alias));

                fields.add(field(f.getQualifiedName(), f.getType()).as(alias));
            }
            else{
                fields.add(field(f.getQualifiedName(), f.getType()));
            }


        }

        return fields;
    }



}
