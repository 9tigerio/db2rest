package com.homihq.db2rest.rest.query.model;

import java.util.List;

public record Table(String schema, String name, String alias, List<Column> columns) {
}
