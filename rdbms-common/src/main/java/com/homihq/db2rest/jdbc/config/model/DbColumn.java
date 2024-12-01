package com.homihq.db2rest.jdbc.config.model;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@SuppressWarnings({"java:S1123", "java:S1133", "java:S6355"})
public record DbColumn(
        String tableName,
        String name,
        String alias,
        String tableAlias,
        boolean pk,
        String columnDataTypeName,
        boolean generated,
        boolean autoIncremented,
        Class<?> typeMappedClass,
        String coverChar,
        String jsonParts
) {

    @Deprecated
    private String getQuotedName() {
        if (StringUtils.isBlank(jsonParts)) {
            return coverChar + name + coverChar;
        } else {
            return coverChar + name + coverChar + jsonParts;
        }
    }

    @Deprecated
    private String getQuotedAlias() {
        return coverChar + alias + coverChar;
    }

    @Deprecated
    public String render() {
        return tableAlias + "." + getQuotedName();
    }

    @Deprecated
    public String renderWithAlias() {

        String firstPart = tableAlias + "." + getQuotedName();

        if (StringUtils.isNotBlank(alias)) {

            return firstPart + " as " + getQuotedAlias();
        }

        return firstPart;
    }


    @Deprecated
    public String getAliasedName() {
        return tableAlias + "." + name;
    }

    @Deprecated
    public String getAliasedNameParam() {
        return tableAlias + "_" + name;
    }

    @Deprecated
    public boolean isDateTimeFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "TIMESTAMP");
    }

    //TODO move to dialect
    @Deprecated
    public boolean isIntFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "SMALLINT", "BIGINT", "int8", "int4", "BIGINT UNSIGNED", "INTEGER", "NUMBER");
    }

    @Deprecated
    public boolean isStringFamily() {
        return StringUtils.equalsAnyIgnoreCase(columnDataTypeName,
                "VARCHAR", "TEXT", "VARCHAR2");
    }

    public DbColumn copyWithAlias(DbAlias columnAlias) {

        log.debug("columnDataTypeName - {}", columnDataTypeName);
        log.debug("full Column name - {}", columnAlias.jsonParts());

        return new DbColumn(tableName, name, columnAlias.alias(), tableAlias,
                pk, columnDataTypeName, generated, autoIncremented, typeMappedClass, coverChar, columnAlias.jsonParts());
    }

    public DbColumn copyWithTableAlias(String tableAlias) {
        return new DbColumn(tableName, name, alias, tableAlias,
                pk, columnDataTypeName, generated, autoIncremented, typeMappedClass, coverChar, "");
    }
}
