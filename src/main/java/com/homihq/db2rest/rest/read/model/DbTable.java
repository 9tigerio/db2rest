package com.homihq.db2rest.rest.read.model;

import com.homihq.db2rest.exception.InvalidColumnException;
import org.apache.commons.lang3.StringUtils;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

public record DbTable(String schema, String name, String alias, Table table) {

    public Column lookupColumn(String columnName) {
        return table
            .getColumns()
            .stream()
            .filter(column -> StringUtils.equalsIgnoreCase(columnName, column.getName()))
            .findFirst()
            .orElseThrow(()-> new InvalidColumnException(name, columnName));
    }
}
