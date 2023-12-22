package com.homihq.db2rest.rest.query.helper.model;

import org.apache.commons.lang3.StringUtils;

public record SelectColumn(String tableName, String tableAlias, String columnName, String columnAlias, boolean root) {

    public String getCol() {

        if(StringUtils.isBlank(columnAlias)) {
            return tableAlias + "." + columnName;
        }
        else{
            return tableAlias + "." + columnName + " as " + "\""+ columnAlias + "\"";
        }
    }
}
