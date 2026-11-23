package com.homihq.db2rest.dtos;

import java.util.List;

import com.homihq.db2rest.auth.data.RoleFilterContext;

public record BulkContext(
                String dbId,
                String schemaName,
                String tableName,
                List<String> includeColumns,
                boolean tsIdEnabled,
                List<String> sequences,
                int rows,
                RoleFilterContext roleFilterContext) {
}
