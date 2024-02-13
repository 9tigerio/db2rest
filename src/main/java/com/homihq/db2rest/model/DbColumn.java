package com.homihq.db2rest.model;


import schemacrawler.schema.Column;

import java.sql.JDBCType;

public record DbColumn(String tableName, String name, JDBCType jdbcType, Column column, String alias, String tableAlias) {

    public String render() {
        return tableAlias + "."+ name ;
    }

    public String renderWithAlias() {
        return tableAlias + "."+ name + " " + alias;
    }

    public String getAliasedName() {return tableAlias + "."+ name;}

    public String getAliasedNameParam() {return tableAlias + "_"+ name;}
}
