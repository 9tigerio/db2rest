package com.homihq.db2rest.rest.handler;

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



}
