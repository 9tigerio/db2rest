package com.homihq.db2rest.jdbc.config.model;

public record DbSort(String table, String tableAlias, String column, String sortDirection) {

    public String render() {
        return tableAlias + "." + column + " " + sortDirection + " ";
    }
}
