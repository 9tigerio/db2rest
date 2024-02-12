package com.homihq.db2rest.rest.read.model;


import schemacrawler.schema.Column;

import java.sql.JDBCType;

public record DbColumn(String tableName, String name, JDBCType jdbcType, Column column, String alias, String tableAlias) {

    public String render() {
        return tableAlias + "."+ name + " " + alias;
    }
}
