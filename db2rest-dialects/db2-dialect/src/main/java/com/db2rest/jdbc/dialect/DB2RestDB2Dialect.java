package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.db2rest.jdbc.dialect.model.Database;
import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
public class DB2RestDB2Dialect extends Dialect {

    public DB2RestDB2Dialect(ObjectMapper objectMapper) {
        super(objectMapper, "\"");
    }

    @Override
    public boolean isSupportedDb(String productName, int majorVersion) {
        return StringUtils.equalsIgnoreCase(productName, Database.DB2.getProductName()) ||
               StringUtils.containsIgnoreCase(productName, "DB2") ||
               StringUtils.containsIgnoreCase(productName, "UDB");
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);
                
                if (value == null) {
                    continue;
                }

                String columnDataTypeName = table.getColumnDataTypeName(columnName);

                // Handle JSON data types (if DB2 supports JSON)
                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json", "clob")) {
                    data.put(columnName, getObjectMapper().writeValueAsString(value));
                } 
                // Handle timestamp data types
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "TIMESTAMP", "DATETIME")) {
                    LocalDateTime v = convertToLocalDateTime((String) value);
                    data.put(columnName, v);
                }
                // Handle XML data types (DB2 specific)
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "XML")) {
                    data.put(columnName, value.toString());
                }
            }
        } catch (Exception exception) {
            throw new GenericDataAccessException("Error processing DB2 data types: " + exception.getMessage());
        }
    }

    @Override
    public String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp) {
        if (deleteOp) {
            // For DELETE operations, DB2 doesn't support table aliases in some contexts
            return getQuotedName(table.schema()) + "." + getQuotedName(table.name());
        }
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name()) + " " + table.alias();
    }

    @Override
    public String renderTableNameWithoutAlias(DbTable table) {
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name());
    }

    private String getQuotedName(String name) {
        return getCoverChar() + name + getCoverChar();
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
        // DB2 supports batch return keys
        return true;
    }

    @Override
    public String getReadSqlTemplate() {
        return "read";
    }

    @Override
    public String getDeleteSqlTemplate() {
        return "delete";
    }

    @Override
    public String getUpdateSqlTemplate() {
        return "update";
    }

    @Override
    public String getInsertSqlTemplate() {
        return "insert";
    }

    @Override
    public String getCountSqlTemplate() {
        return "count";
    }

    @Override
    public String getExistSqlTemplate() {
        return "exists";
    }

    @Override
    public String getFindOneSqlTemplate() {
        return "find-one";
    }
}
