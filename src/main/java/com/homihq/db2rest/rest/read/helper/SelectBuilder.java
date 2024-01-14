package com.homihq.db2rest.rest.read.helper;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectBuilder{

    private final SchemaManager schemaManager;
    private final Db2RestConfigProperties db2RestConfigProperties;

    public void build(ReadContext context) {
        context.setTables(createTables(context));
        context.createSelect();

    }

    private List<MyBatisTable> createTables(ReadContext context) {
        List<MyBatisTable> tables = new ArrayList<>();
        log.info("context.select - {}", context.select);
        //split to get all fragments
        String [] tabCols = context.select.split(";");

        int counter = 0;

        log.info("tabCols - {}", tabCols.length);

        //process the fragments
        for(String tabCol : tabCols) {
            List<MyBatisTable> myBatisTables;

            //check for presence of open '(' and close ')' brackets
            //now check for embedded table and columns.
            if(tabCol.contains("(") && tabCol.contains(")")) { //join tables

                String joinTableName = tabCol.substring(0, tabCol.indexOf("("));
                //look for columns
                String colString = tabCol.substring(tabCol.indexOf("(")  + 1 , tabCol.indexOf(")"));
                myBatisTables = createTables(context.schemaName, joinTableName, colString, counter);
            }
            else{ //root table

                log.info("Creating root tables");
                myBatisTables = createTables(context.schemaName, context.tableName, tabCol, counter);

                log.info("myBatisTables - {}", myBatisTables);

                for(MyBatisTable table : myBatisTables) {
                    table.setRoot(true);
                }

                //TODO - multiple root tables and no other table = union query, else unsupported exception

            }
            tables.addAll(myBatisTables);

            counter++;
        }


        return tables;
    }


    private List<MyBatisTable> createTables(String schemaName, String tableName,  String colStr, int counter) {

        String sName = schemaName;
        String tName = tableName;


        if(!this.db2RestConfigProperties.getMultiTenancy().isEnabled()) {
            String [] tableNameParts = tableName.split("|.");

            if(tableNameParts.length == 2) { //table name contains schema
                sName = tableNameParts[0];
                tName = tableNameParts[1];
            }
        }

        if(StringUtils.isNotBlank(sName)) {
            MyBatisTable table = schemaManager.findTable(sName, tName, counter);

            addColumns(table, colStr);

            return List.of(table);
        }
        else {
            List<MyBatisTable> tables = schemaManager.findTables(tName);

            for(MyBatisTable table : tables) {
                addColumns(table, colStr);
            }

            return tables;
        }


    }

    private void addColumns(MyBatisTable table, String colStr) {
        String[] cols = colStr.split(",");

        if(cols.length == 1) { //no columns specified
            //TODO - do it only for root table.
            table.addAllColumns();
        }
        else {
            for (String col : cols) {
                addColumn(table, col);
            }
        }

    }

    private void addColumn(MyBatisTable table,String colStr) {
        String colName;
        String alias;

        //check if there is column Alias
        String [] c = colStr.split(":");

        if(c.length == 2) {
            colName = c[0];
            alias = c[1];

        }
        else{ //no column alias
            colName = c[0];
            alias = colName;
        }

        table.addColumn(colName, alias);
    }



}
