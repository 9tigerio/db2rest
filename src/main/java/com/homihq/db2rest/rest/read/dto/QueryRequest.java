package com.homihq.db2rest.rest.read.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

public record QueryRequest(@NotEmpty(message = "Sql statement cannot be empty") String sql, Map<String, Object> params,
                           boolean single) {
}
