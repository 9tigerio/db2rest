package com.db2rest.jdbc.dialect.model;

public record DbSort(String table, String tableAlias, String column, String sortDirection) {

    public String render() {
        return tableAlias + "." + column + " " + sortDirection + " ";
    }
}
