package com.homihq.db2rest.rest.query.model;

import java.util.List;

public record JoinTable(String name, List<String> columns, String filter) {
}
