package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.Database;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
public class MsSQLServerDialect extends Dialect {
    public MsSQLServerDialect(ObjectMapper objectMapper) {
        super(objectMapper, "\"");
    }

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
                    data.put(columnName, getObjectMapper().writeValueAsString(value));
                } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "datetime")) {
                    LocalDateTime v = convertToLocalDateTime((String) value);
                    data.put(columnName, v);
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
        return getCoverChar() + name + getCoverChar();
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

    private LocalDateTime convertToLocalDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new GenericDataAccessException("Error converting to LocalDateTime type - " + e.getLocalizedMessage());
        }
    }
}
