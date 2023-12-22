package com.homihq.db2rest.rest.query.helper.model;

import java.util.List;

public record SelectColumns(List<SelectColumn> selectColumnList) {

    public String getSelect() {

        if(selectColumnList.isEmpty()) {
            return " * ";
        }
        else{

            List<String> cols =
                    selectColumnList.stream().map(SelectColumn::getCol).toList();

            return String.join(" , ", cols);
        }

    }

    public String getTables(String rootTable) {
        if(selectColumnList.isEmpty()) {
            return " " + rootTable + " ";
        }
        else{

            List<String> tables =
                    selectColumnList.stream().map(i -> i.tableName() + " " + i.tableAlias()).toList()
                            .stream().distinct().toList();

            return String.join(" , ", tables);
        }
    }
}
