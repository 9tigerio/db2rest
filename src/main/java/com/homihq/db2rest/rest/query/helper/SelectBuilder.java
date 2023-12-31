package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.rest.query.model.JoinTable;
import com.homihq.db2rest.rest.query.model.RColumn;
import com.homihq.db2rest.rest.query.model.RJoin;
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
import static com.homihq.db2rest.schema.NameUtil.*;

@Component
@Slf4j
public class SelectBuilder implements SqlQueryPartBuilder{


    public void build(QueryBuilderContext context) {

        if(StringUtils.isBlank(context.select)) { // use asterix on root table
            context.setAstrix(true);
        }
        else{
            List<RTable> tables =
                    parseSelect(
                    context.schemaName,
                    context.getTableName(), context.select);

            context.setRTables(tables);
        }

    }

    public void postProcess(QueryBuilderContext context) {
        List<RColumn> columns =
        context.getRTables().stream()
                .flatMap(i -> i.getColumns().stream())
                .toList();


        if(!context.isAstrix()) {
            //check if any column in join table list

            List<RColumn> columnList = new ArrayList<>();

            List<String> exclusionTables =
                    context.rJoins.stream().map(RJoin::getTableName)
                            .distinct().toList();


            // root table columns
            for(RColumn rColumn : columns) {
                for(String tName : exclusionTables) {
                    if(!StringUtils.equalsIgnoreCase(rColumn.getTable(), tName)) {
                        columnList.add(rColumn);
                    }
                }
            }

            for(RJoin join : context.rJoins) {

                for(RColumn rColumn : columns) {

                    if(StringUtils.equalsIgnoreCase(join.getTableName(), rColumn.getTable())) {
                        RColumn rc = createColumn(join.getTableName(), join.getAlias(), rColumn.getName());
                        columnList.add(rc);
                    }
                }

            }


            context.setRColumns(columnList);
        }

    }


    private RTable createTable(String schema, String tableName,  String colStr, int counter) {
        RTable table = new RTable();
        table.setSchema(schema);
        table.setName(tableName);
        table.setAlias(getAlias(counter, ""));

        List<RColumn> columnList = new ArrayList<>();

        String[] cols = colStr.split(",");

        for (String col : cols) {

            RColumn rColumn = createColumn(tableName, table.getAlias() ,col);
            columnList.add(rColumn);
        }

        table.setColumns(columnList);

        return table;
    }

    private static RColumn createColumn(String tableName, String tableAlias,String colStr) {
        RColumn rColumn = new RColumn();
        rColumn.setTable(tableName);
        rColumn.setTableAlias(tableAlias);

        //check if there is column Alias
        String [] c = colStr.split(":");

        if(c.length == 2) {
            rColumn.setName(c[0]);
            rColumn.setAlias(c[1]);
        }
        else{ //no column alias
            rColumn.setName(colStr);
        }
        return rColumn;
    }

    private List<RTable> parseSelect(String schema, String rootTableName, String select) {
        List<RTable> tables = new ArrayList<>();

        //split to get all tables n columns
        String [] tabCols = select.split(";");

        int counter = 0;

        //now check for embedded table and columns.
        for(String tabCol : tabCols) {
            RTable rTable;
            //check for presence of open '(' and close ')' brackets
            if(tabCol.contains("(") && tabCol.contains(")")) { //join table

                String joinTableName = tabCol.substring(0, tabCol.indexOf("("));
                //look for columns
                String colString = tabCol.substring(tabCol.indexOf("(")  + 1 , tabCol.indexOf(")"));
                rTable = createTable(schema, joinTableName, colString, counter);
            }
            else{
                rTable = createTable(schema, rootTableName, tabCol, counter);

            }
            tables.add(rTable);

            counter++;
        }


        return tables;
    }

    @Deprecated
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

    @Deprecated
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
