package com.homihq.db2rest.rest.read.model;


import schemacrawler.schema.Column;

import java.sql.JDBCType;

public record DbColumn(String tableName, String name, JDBCType jdbcType, Column column, String alias) {

    public String render() {
        return name + " " + alias;
    }
}
