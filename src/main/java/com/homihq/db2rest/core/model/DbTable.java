package com.homihq.db2rest.core.model;

import com.homihq.db2rest.exception.InvalidColumnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.List;


@Slf4j
public record DbTable(String schema, String name, String fullName, String alias, List<DbColumn> dbColumns) {

    public String render() {
        return name + " " + alias;
    }


    public DbColumn buildColumn(String columnName) {
        log.debug("columnName - {}", columnName);

        DbAlias dbAlias = getAlias(columnName);

        return getDbColumn(dbAlias);
    }

    private DbColumn getDbColumn(DbAlias dbAlias) {
        return
        this.dbColumns.stream()
                .filter(dbColumn -> StringUtils.equalsAnyIgnoreCase(dbAlias.name(), dbColumn.name()))
                .findFirst().orElseThrow(() -> new InvalidColumnException(name,dbAlias.name()));
    }

    public DbAlias getAlias(String name) {
        String [] aliasParts = name.split(":");

        if(aliasParts.length == 2) {
            return new DbAlias(aliasParts[0], aliasParts[1]);
        }
        else {
            return new DbAlias(aliasParts[0], "");
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

    public String [] getKeyColumnNames() {
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
                .orElseThrow(()-> new InvalidColumnException(name, columnName));
    }
}
