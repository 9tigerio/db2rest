package com.db2rest.jdbc.dialect.model;

import com.homihq.db2rest.core.exception.InvalidColumnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.List;

@Slf4j
public record DbTable(String schema, String name, String fullName, String alias,
                      List<DbColumn> dbColumns, String type, String coverChar) {

    public String render() {
        return fullName + " " + alias;
    }

    public DbTable copyWithAlias(String tableAlias) {
        List<DbColumn> columns =
                dbColumns.stream()
                        .map(col -> col.copyWithTableAlias(tableAlias))
                        .toList();

        return new DbTable(schema, name, fullName, tableAlias, columns, type, coverChar);
    }

    public DbColumn buildColumn(String columnName) {
        DbAlias dbAlias = getAlias(columnName);
        return getDbColumn(dbAlias);
    }

    private DbColumn getDbColumn(DbAlias dbAlias) {
        return
                this.dbColumns.stream()
                        .filter(dbColumn -> Strings.CI.equalsAny(dbAlias.name(), dbColumn.name()))
                        .map(dbColumn -> dbColumn.copyWithAlias(dbAlias))
                        .findFirst().orElseThrow(() -> new InvalidColumnException(name, dbAlias.name()));
    }

    private DbAlias getAlias(String name) {
        String[] aliasParts = name.split(":");

        String columnName = aliasParts[0];
        String colName = columnName;
        String jsonParts = "";

        if (StringUtils.contains(columnName, "->")) {
            colName = columnName.substring(0, columnName.indexOf("->"));
            jsonParts = columnName.substring(columnName.indexOf("->"));
        } else if (StringUtils.contains(columnName, "->>")) {
            colName = columnName.substring(0, columnName.indexOf("->>"));
            jsonParts = columnName.substring(columnName.indexOf("->>"));
        } else if (StringUtils.contains(columnName, "#>")) {

            colName = columnName.substring(0, columnName.indexOf("#>"));
            jsonParts = columnName.substring(columnName.indexOf("#>"));
            jsonParts = jsonParts.replace(".", ","); //specific attribute
        } else if (StringUtils.contains(columnName, "#>>")) {

            colName = columnName.substring(0, columnName.indexOf("#>>"));
            jsonParts = columnName.substring(columnName.indexOf("#>>"));
            jsonParts = jsonParts.replace(".", ","); //specific attribute
        } else if (StringUtils.contains(columnName, "**")) {
            colName = columnName.substring(0, columnName.indexOf("**"));
            jsonParts = "->>" + "'" + columnName.substring(columnName.indexOf("**") + 2) + "'";
        } else if (StringUtils.contains(columnName, "*")) {
            colName = columnName.substring(0, columnName.indexOf("*"));
            jsonParts = "->" + "'" + columnName.substring(columnName.indexOf("*") + 1) + "'";
        }

        if (aliasParts.length == 2) {
            return new DbAlias(colName.trim(), aliasParts[1], jsonParts);
        } else {
            return new DbAlias(colName.trim(), "", jsonParts);
        }
    }

    public List<DbColumn> buildColumns() {
        return dbColumns;
    }

    public List<DbColumn> buildPkColumns() {
        return dbColumns
                .stream()
                .filter(DbColumn::pk)
                .toList();
    }

    public String[] getKeyColumnNames() {
        return buildPkColumns()
                .stream()
                .map(DbColumn::name)
                .toList().toArray(String[]::new);
    }

    public String getColumnDataTypeName(String columnName) {
        return lookupColumn(columnName).columnDataTypeName();
    }

    private DbColumn lookupColumn(String columnName) {

        return dbColumns.stream()
                .filter(dbColumn -> StringUtils.equalsAnyIgnoreCase(columnName, dbColumn.name()))
                .findFirst()
                .orElseThrow(() -> new InvalidColumnException(name, columnName));
    }
}
