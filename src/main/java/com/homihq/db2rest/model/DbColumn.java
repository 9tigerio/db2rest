package com.homihq.db2rest.model;


import org.apache.commons.lang3.StringUtils;
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

    @Deprecated
    public boolean isPk() {
        return column.isPartOfPrimaryKey();
    }

    @Deprecated
    public boolean hasDefaultValue() { return column.hasDefaultValue();}

    @Deprecated
    public Object getDefaultValue() {return column.getDefaultValue();}

    @Deprecated
    public boolean isDateTimeFamily() {
        return StringUtils.equalsAnyIgnoreCase(column.getColumnDataType().getName(), "TIMESTAMP");
    }

    public boolean isIntFamily() {
        return StringUtils.equalsAnyIgnoreCase(column.getColumnDataType().getName(),
                "SMALLINT");
    }
    public boolean isStringFamily() {
        return StringUtils.equalsAnyIgnoreCase(column.getColumnDataType().getName(),
                "VARCHAR");
    }

}
