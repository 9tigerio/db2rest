package com.homihq.db2rest.rest.create.dto;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

public record CreateContext(List<String> insertableColumns) {

    public List<String> getColumnNames() {
        return insertableColumns.stream()
                .toList();
    }

    public List<String> getParamNames() {
        return insertableColumns.stream()
                .map(i -> ":" + i).toList();
    }

    public String renderColumns() {
        return StringUtils.join(getColumnNames(), ",");
    }

    public String renderParams() {
        return StringUtils.join(getParamNames(), ",");
    }
}
