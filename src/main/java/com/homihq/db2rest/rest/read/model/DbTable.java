package com.homihq.db2rest.rest.read.model;

import com.homihq.db2rest.exception.InvalidColumnException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.List;

import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;

@Slf4j
public record DbTable(String schema, String name, String alias, Table table) {

    public String render() {
        return name + " " + alias;
    }

    public Column lookupColumn(String columnName) {
        return table
            .getColumns()
            .stream()
            .filter(column -> StringUtils.equalsIgnoreCase(columnName, column.getName()))
            .findFirst()
            .orElseThrow(()-> new InvalidColumnException(name, columnName));
    }


    public DbColumn buildColumn(String columnName) {
        log.info("columnName - {}", columnName);

        DbAlias dbAlias = getAlias(columnName);

        Column column = lookupColumn(dbAlias.name());

        return new DbColumn(name, column.getName(), getJdbcType(column) , column, dbAlias.alias(), alias);
    }

    public DbAlias getAlias(String name) {
        String [] aliasParts = name.split(":");

        if(aliasParts.length == 2) {
            return new DbAlias(aliasParts[0], aliasParts[1]);
        }
        else {
            return new DbAlias(aliasParts[0], null);
        }
    }

    public List<DbColumn> buildColumns() {
        return table.getColumns()
                .stream()
                .map(column ->
                        new DbColumn(name, column.getName(),getJdbcType(column) , column,
                                "", alias))
                .toList();
    }
}
