package com.homihq.db2rest.core.model;

import org.apache.commons.lang3.StringUtils;

public record DbColumn(String tableName, String name, String alias, String tableAlias,
                       boolean pk, String columnDataTypeName, boolean generated, boolean autoIncremented
,Class<?> typeMappedClass) {

    public String render() {
        return tableAlias + "."+ name ;
    }

    public String renderWithAlias() {
        return tableAlias + "."+ name + " " + alias;
    }

    public String getAliasedName() {return tableAlias + "."+ name;}

    public String getAliasedNameParam() {return tableAlias + "_"+ name;}

    @Deprecated
    public boolean isDateTimeFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "TIMESTAMP");
    }

    public boolean isIntFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "SMALLINT", "int8", "BIGINT UNSIGNED","INTEGER");
    }
    public boolean isStringFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "VARCHAR","TEXT");
    }

}
