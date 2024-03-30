package com.homihq.db2rest.jdbc.dto;

import com.homihq.db2rest.core.model.DbTable;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

public record CreateContext(DbTable table, List<String> insertableColumns, List<InsertableColumn> insertableColumnList) {

    private List<String> getColumnNames() {
        return insertableColumnList.stream().map(c -> c.columnName)
                .toList();
    }

    private List<String> getParamNames() {
        return insertableColumnList.stream()

                .map(col ->
                        StringUtils.isBlank(col.sequence) ? ":" + col.columnName : col.sequence)
                .toList();
    }

    public String renderColumns() {
        return StringUtils.join(getColumnNames(), ",");
    }

    public String renderParams() {
        return StringUtils.join(getParamNames(), ",");
    }
}
