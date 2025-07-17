package com.homihq.db2rest.jdbc.dto;

import java.util.List;

public record Placeholder(String namedParam, List<String> filters) {
}
