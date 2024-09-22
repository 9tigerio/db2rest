package com.homihq.db2rest.jdbc.config.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.Database;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MsSQLServerDialect implements Dialect {

    private static final String COVER_CHAR = "\"";

    private final ObjectMapper objectMapper;

    // https://github.com/Microsoft/mssql-jdbc/issues/245
    @Override
    public boolean supportBatchReturnKeys() {
        return false;
    }

    @Override
    public boolean isSupportedDb(String productName, int majorVersion) {
        return StringUtils.equalsIgnoreCase(productName, Database.MSSQL.getProductName());
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.getColumnDataTypeName(columnName);

                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {
                    data.put(columnName, objectMapper.writeValueAsString(value));
                }
            }
        } catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
        }

    }

    @Override
    public String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp) {
        return getQuotedName(table.schema())
                + "." + getQuotedName(table.name())
                + " " + table.alias();
    }

    @Override
    public String renderTableNameWithoutAlias(DbTable table) {
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name());
    }

    private String getQuotedName(String name) {
        return COVER_CHAR + name + COVER_CHAR;
    }

    @Override
    public String getDeleteSqlTemplate() {
        return "delete-mssql";
    }

    @Override
    public String getExistSqlTemplate() {
        return "exists-mssql";
    }

    @Override
    public String getReadSqlTemplate() {
        return "read-mssql";
    }

    @Override
    public String getUpdateSqlTemplate() {
        return "update-mssql";
    }

}
