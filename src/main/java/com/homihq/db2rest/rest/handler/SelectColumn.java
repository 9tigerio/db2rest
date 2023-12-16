package com.homihq.db2rest.rest.handler;

import org.apache.commons.lang3.StringUtils;

public record SelectColumn(String tableName, String columnName, String columnAlias) {

    public String getCol() {

        if(StringUtils.isBlank(columnAlias)) {
            return columnName;
        }
        else{
            return columnName + " as " + "\""+ columnAlias + "\"";
        }
    }
}
