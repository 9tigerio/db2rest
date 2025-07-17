package com.homihq.db2rest.dtos;

import java.util.List;

public record FileUploadContext(
        String dbId,
        String schemaName,
        String tableName,
        List<String> includeColumns,
        boolean tsIdEnabled,
        List<String> sequences,
        int rows
) {
}
