package com.homihq.db2rest.jdbc.config.dialect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"java:S1123", "java:S1172", "java:S1133", "java:S6355"})
public abstract class Dialect {
    private final ObjectMapper objectMapper;
    private final String coverChar;

    protected Dialect(ObjectMapper objectMapper, String coverChar) {
        this.objectMapper = objectMapper;
        this.coverChar = coverChar;
    }

    public abstract boolean isSupportedDb(String productName, int majorVersion);

    public abstract void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data);

    public abstract String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp);

    public abstract String renderTableNameWithoutAlias(DbTable table);

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    protected String getCoverChar() {
        return coverChar;
    }

    public boolean supportBatchReturnKeys() {
        return true;
    }

    public boolean supportAlias() {
        return true;
    }

    public int getMajorVersion() {
        return -1;
    }

    public String getAliasedName(DbColumn dbColumn, boolean deleteOp) {
        return dbColumn.tableAlias() + "." + dbColumn.name();
    }

    public String getAliasedNameParam(DbColumn dbColumn, boolean deleteOp) {
        return dbColumn.tableAlias() + "_" + dbColumn.name();
    }

    public List<Object> parseListValues(List<String> values, Class<?> type) {
        return
                values.stream()
                        .map(v -> processValue(v, type, null))
                        .toList();
    }

    //TODO use Spring converter
    @Deprecated
    public Object processValue(String value, Class<?> type, String format) {
        if (String.class == type) {
            return value;
        } else if (Boolean.class == type || boolean.class == type) {
            boolean aBoolean = Boolean.parseBoolean(value);
            return aBoolean ? "1" : "0";
        } else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(value);
        } else if (Long.class == type || long.class == type) {
            return Long.valueOf(value);
        } else if (Short.class == type || short.class == type) {
            return Short.valueOf(value);
        } else if (java.sql.Date.class == type) {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        } else if (java.sql.Timestamp.class == type) {
            return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            return value;
        }

    }

    public List<String> convertToStringArray(Object object) {
        return List.of();
    }

    public Object convertJsonToVO(Object object) {
        return null;
    }

    public String getCountSqlTemplate() {
        return "count";
    }

    public String getDeleteSqlTemplate() {
        return "delete";
    }

    public String getExistSqlTemplate() {
        return "exists";
    }

    public String getFindOneSqlTemplate() {
        return "find-one";
    }

    public String getInsertSqlTemplate() {
        return "insert";
    }

    public String getReadSqlTemplate() {
        return "read";
    }

    public String getUpdateSqlTemplate() {
        return "update";
    }
}
