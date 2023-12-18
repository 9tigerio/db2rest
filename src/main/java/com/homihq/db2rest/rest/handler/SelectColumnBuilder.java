package com.homihq.db2rest.rest.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SelectColumnBuilder {

    public SelectColumns build(String tableName, String tableNameAlias, List<String> columnNames, boolean root) {
        List<SelectColumn> selectColumnList = new ArrayList<>();

        for(String columnName : columnNames) {
            String [] parts = columnName.split(":");
            String alias = null;
            String colName;
            if(parts.length > 1) {
                alias = parts[0]; //process further to check for association

                colName = parts[1];

            }
            else{
                colName = parts[0];
            }

            selectColumnList.add(new SelectColumn(tableName, tableNameAlias, colName, alias, root));
        }


        return new SelectColumns(selectColumnList);
    }


    public SelectColumns buildEmbeded(List<String> joinTables) {

        List<SelectColumn> joinTableColumns = new ArrayList<>();

        for(String joinTable : joinTables) {
            //handle table
            String jt = joinTable.substring(0 , joinTable.indexOf("("));
            String [] joinTableParts = jt.split(":");

            String tableAlias = null;
            String tableName;
            if(joinTableParts.length > 1) {
                tableAlias = joinTableParts[0];

                tableName = joinTableParts[1];

            }
            else{
                tableAlias = joinTableParts[0];
                tableName = joinTableParts[0];
            }

            //Now handle columns

            String joinTableCols = joinTable.substring(joinTable.indexOf("(")+1, joinTable.indexOf(")"));

            if(StringUtils.isBlank(joinTableCols)) throw new RuntimeException("No column found for embedded tables.");


            List<String> columns = List.of(joinTableCols.split(","));
            SelectColumns selectColumns =
            build(tableName, tableAlias, columns, false);

            joinTableColumns.addAll(selectColumns.selectColumnList());
        }


        return new SelectColumns(joinTableColumns);

    }
}
