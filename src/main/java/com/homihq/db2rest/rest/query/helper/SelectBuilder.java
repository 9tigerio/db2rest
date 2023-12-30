package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.rest.query.model.JoinTable;
import com.homihq.db2rest.rest.query.model.RColumn;
import com.homihq.db2rest.rest.query.model.RTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jooq.impl.DSL.*;


@Component
@Slf4j
public class SelectBuilder {

    public void build(QueryBuilderContext context) {
        List<String> columns = StringUtils.isBlank(context.select) ?  List.of() : List.of(context.select.split(";"));

        if(StringUtils.isBlank(context.select)) { // use asterix on root table
            context.buildAstrix();
        }
        else{
            List<RTable> tables =
            processSelect(
                    context.schemaName,
                    context.getTableName(), context.select);

            context.buildSelectColumns(tables);
        }

    }

    private List<RTable> processSelect(String schema, String rootTableName, String select) {
        List<RTable> tables = new ArrayList<>();

        //split to get all tables n columns
        String [] tabCols = select.split(";");

        //now check for embedded table and columns.
        for(String tabCol : tabCols) {
            RTable rTable;
            //check for presence of open '(' and close ')' brackets
            if(tabCol.contains("(") && tabCol.contains(")")) { //join table

                String joinTableName = tabCol.substring(0, tabCol.indexOf("("));
                //look for columns
                String colString = tabCol.substring(tabCol.indexOf("(")  + 1 , tabCol.indexOf(")"));
                rTable = getTable(schema, joinTableName, colString);
            }
            else{
                rTable = getTable(schema, rootTableName, tabCol);

            }
            tables.add(rTable);
        }
        return tables;
    }

    public RTable getTable(String schema, String tableName, String colStr) {
        RTable table = new RTable();
        table.setSchema(schema);
        table.setName(tableName);

        List<RColumn> columnList = new ArrayList<>();

        String[] cols = colStr.split(",");

        for (String col : cols) {
            log.info(tableName + "." + col);
            RColumn rColumn = new RColumn();
            rColumn.setTable(tableName);
            rColumn.setName(col);

            columnList.add(rColumn);
        }

        table.setColumns(columnList);

        return table;
    }

    public List<Field<?>> build(Table<?> table, List<String> columnNames, Table<?> jTable, JoinTable jt) {

        List<Field<?>> fields = new ArrayList<>();

        addFieldsByTable(table, columnNames, fields);

        if(Objects.nonNull(jt) &&
                Objects.nonNull(jt.columns()) &&
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
                    .findFirst().orElseThrow(() -> new InvalidColumnException(table.getName() , colName));

            if(StringUtils.isNotBlank(alias)) {

                fields.add(field(f.getQualifiedName(), f.getType()).as(alias));
            }
            else{
                fields.add(field(f.getQualifiedName(), f.getType()));
            }

        }
    }


}
