package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.db2rest.jdbc.dialect.model.Database;
import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Strings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
public class SQLiteDialect extends Dialect {

    public SQLiteDialect(ObjectMapper objectMapper) {
        super(objectMapper, "`");
    }

    @Override
    public boolean isSupportedDb(String productName, int majorVersion) {
        return Strings.CI.equalsAny(productName, Database.SQLITE.getProductName());
    }

    @Override
    public boolean supportAlias() {
        return false;
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.getColumnDataTypeName(columnName);

                if (Strings.CI.equalsAny(columnDataTypeName, "json")) {
                    data.put(columnName, getObjectMapper().writeValueAsString(value));
                } else if (Strings.CI.equalsAny(columnDataTypeName, "TIMESTAMP", "DATETIME")) {
                    LocalDateTime v = convertToLocalDateTime((String) value);
                    data.put(columnName, v);
                }
            }
        } catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
        }
    }

    private String getQuotedName(String name) {
        return getCoverChar() + name + getCoverChar();
    }

    @Override
    public String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp) {
        if (deleteOp) {
            return getQuotedName(table.name());
        }
        return getQuotedName(table.name()) + " " + table.alias();
    }

    @Override
    public String renderTableNameWithoutAlias(DbTable table) {
        return getQuotedName(table.name());
    }

    private LocalDateTime convertToLocalDateTime(String value) {
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            throw new GenericDataAccessException("Error converting to LocalDateTime type - " + e.getLocalizedMessage());
        }
    }

    @Override
    public LocalDateTime convertTimestamp(String value) {
        return convertToLocalDateTime(value);
    }

    @Override
    public boolean supportBatchReturnKeys() {
        return false;
    }
}