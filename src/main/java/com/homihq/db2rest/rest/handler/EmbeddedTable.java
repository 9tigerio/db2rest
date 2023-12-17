package com.homihq.db2rest.rest.handler;

import java.util.List;

public record EmbeddedTable(String tableName, String tableAlias, List<SelectColumn> columns) {
}
