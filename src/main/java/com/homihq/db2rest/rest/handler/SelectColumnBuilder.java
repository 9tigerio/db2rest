package com.homihq.db2rest.rest.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SelectColumnBuilder {

    public SelectColumns build(String tableName, List<String> columnNames) {
        List<SelectColumn> selectColumnList = new ArrayList<>();

        for(String columnName : columnNames) {
            String [] parts = columnName.split(":");
            String alias = null;
            String colName;
            if(parts.length > 1) {
                alias = parts[0]; //process further to check for association

                colName = parts[1];

                findEmbededTable(colName);
            }
            else{
                colName = parts[0];
            }

            selectColumnList.add(new SelectColumn(tableName, tableName, colName, alias, true));
        }


        return new SelectColumns(selectColumnList);
    }

    private EmbeddedTable findEmbededTable(String colName) {
        if(StringUtils.endsWith(colName, ")")) {

        }


        return null;
    }
}
