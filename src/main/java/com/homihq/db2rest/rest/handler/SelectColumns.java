package com.homihq.db2rest.rest.handler;

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
}
