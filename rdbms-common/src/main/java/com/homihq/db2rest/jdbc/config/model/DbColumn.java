package com.homihq.db2rest.jdbc.config.model;

import org.apache.commons.lang3.StringUtils;

public record DbColumn(String tableName, String name, String alias, String tableAlias,
                       boolean pk, String columnDataTypeName, boolean generated, boolean autoIncremented
                        ,Class<?> typeMappedClass , String coverChar) {

    @Deprecated
    private String getQuotedName() {
        return coverChar + name + coverChar;
    }

    @Deprecated
    private String getQuotedAlias() {
        return coverChar + alias + coverChar;
    }

    @Deprecated
    public String render() {
        return tableAlias + "."+ getQuotedName() ;
    }

    @Deprecated
    public String renderWithAlias() {
        if(StringUtils.isNotBlank(alias))
            return tableAlias + "."+ getQuotedName() + " as " + getQuotedAlias();

        return tableAlias + "."+ getQuotedName() + " " + alias;
    }


    @Deprecated
    public String getAliasedName() {return tableAlias + "."+ name;}

    @Deprecated
    public String getAliasedNameParam() {return tableAlias + "_"+ name;}

    @Deprecated
    public boolean isDateTimeFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "TIMESTAMP");
    }

    @Deprecated
    public boolean isIntFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "SMALLINT", "int8", "BIGINT UNSIGNED","INTEGER", "NUMBER");
    }
    @Deprecated
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
