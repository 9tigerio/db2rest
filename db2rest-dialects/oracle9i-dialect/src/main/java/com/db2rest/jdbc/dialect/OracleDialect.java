package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.Database;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
public class OracleDialect extends Dialect {
    public OracleDialect(ObjectMapper objectMapper) {
        super(objectMapper, "\"");
    }

    @Override
    public boolean isSupportedDb(String productName, int majorVersion) {
        return StringUtils.equalsIgnoreCase(productName, Database.ORACLE.getProductName())
                && majorVersion == 9;
    }

    @Override
    public String getReadSqlTemplate() {
        return "read-ora-9";
    }

    @Override
    public boolean supportBatchReturnKeys() {
        return false;
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {

        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.getColumnDataTypeName(columnName);


                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {

                    data.put(columnName, getObjectMapper().writeValueAsString(value));
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
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name()) + " " + table.alias();
    }

    @Override
    public String renderTableNameWithoutAlias(DbTable table) {
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name());
    }
}
