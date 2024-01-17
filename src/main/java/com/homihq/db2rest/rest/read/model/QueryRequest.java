package com.homihq.db2rest.rest.read.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QueryRequest {

    @NotEmpty(message = "Sql statement cannot be empty")
    private String sql;

    private Map<String, Object> params;

    private boolean isSingle;
}
