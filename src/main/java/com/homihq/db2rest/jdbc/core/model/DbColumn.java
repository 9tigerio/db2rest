package com.homihq.db2rest.jdbc.core.model;

import org.apache.commons.lang3.StringUtils;

public record DbColumn(String tableName, String name, String alias, String tableAlias,
                       boolean pk, String columnDataTypeName, boolean generated, boolean autoIncremented
                        ,Class<?> typeMappedClass , String coverChar) {

    private String getQuotedName() {
        return coverChar + name + coverChar;
    }

    private String getQuotedAlias() {
        return coverChar + alias + coverChar;
    }

    public String render() {
        return tableAlias + "."+ getQuotedName() ;
    }

    public String renderWithAlias() {
        if(StringUtils.isNotBlank(alias))
            return tableAlias + "."+ getQuotedName() + " as " + getQuotedAlias();

        return tableAlias + "."+ getQuotedName() + " " + alias;
    }



    public String getAliasedName() {return tableAlias + "."+ name;}

    public String getAliasedNameParam() {return tableAlias + "_"+ name;}

    @Deprecated
    public boolean isDateTimeFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "TIMESTAMP");
    }

    public boolean isIntFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "SMALLINT", "int8", "BIGINT UNSIGNED","INTEGER", "NUMBER");
    }
    public boolean isStringFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "VARCHAR","TEXT", "VARCHAR2");
    }

    public DbColumn copyWithAlias(String columnAlias) {
        return new DbColumn(tableName, name, columnAlias, tableAlias,
                pk, columnDataTypeName, generated, autoIncremented, typeMappedClass, coverChar);
    }

    public DbColumn copyWithTableAlias(String tableAlias) {
        return new DbColumn(tableName, name, alias, tableAlias,
                pk, columnDataTypeName, generated, autoIncremented, typeMappedClass, coverChar);
    }
}
