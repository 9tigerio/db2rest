package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SelectBuilder implements SqlQueryPartBuilder{

    private final SchemaManager schemaManager;

    public void build(QueryContext context) {
        //create tables and alias
        List<MyBatisTable> tables = createTables(context);

        context.setTables(tables);

    }

    private List<MyBatisTable> createTables(QueryContext context) {
        List<MyBatisTable> tables = new ArrayList<>();

        //split to get all fragments
        String [] tabCols = context.select.split(";");

        int counter = 0;

        //process the fragments
        for(String tabCol : tabCols) {
            MyBatisTable table;

            //check for presence of open '(' and close ')' brackets
            //now check for embedded table and columns.
            if(tabCol.contains("(") && tabCol.contains(")")) { //join table

                String joinTableName = tabCol.substring(0, tabCol.indexOf("("));
                //look for columns
                String colString = tabCol.substring(tabCol.indexOf("(")  + 1 , tabCol.indexOf(")"));
                table = createTable(context.schemaName, joinTableName, colString, counter);
            }
            else{ //root table
                table = createTable(context.schemaName, context.tableName, tabCol, counter);

            }
            tables.add(table);

            counter++;
        }


        return tables;
    }

    private MyBatisTable createTable(String schemaName, String tableName,  String colStr, int counter) {

        MyBatisTable table = schemaManager.findTable(schemaName, tableName, counter);

        addColumns(table, colStr);

        return table;
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
