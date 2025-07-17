package com.homihq.db2rest.jdbc.config.dialect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<Object> parseListValues(List<String> values, Class type, String columnDatatypeName) {
        return
                values.stream()
                        .map(v -> processValue(v, type, null,columnDatatypeName))
                        .toList();
    }

    //TODO use Spring converter
    @Deprecated
    public Object processValue(String value, Class<?> type, String format, String columnTypeName) {
        //System.out.println("type " + type);
        if (String.class == type) {
            //return "'" + value + "'";
            return value;
        } else if (Boolean.class == type || boolean.class == type) {
            Boolean aBoolean = Boolean.valueOf(value);
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
            return convertTimestamp(value);

        }
        else if (Object.class == type && "uuid".equals(columnTypeName)) {
            return UUID.fromString(value);
        }
        else {
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

    public Object convertTimestamp(String value){
        return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
