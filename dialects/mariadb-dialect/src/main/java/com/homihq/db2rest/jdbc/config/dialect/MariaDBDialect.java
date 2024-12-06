package com.homihq.db2rest.jdbc.config.dialect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.Database;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class MariaDBDialect implements Dialect {

    private final ObjectMapper objectMapper;

    private String coverChar = "`";

    @Override
    public boolean isSupportedDb(String productName, int majorVersion) {
        return StringUtils.equalsIgnoreCase(productName, Database.MARIADB.getProductName());
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
        }
        catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
        }

    }

    private String getQuotedName(String name) {
        return coverChar + name + coverChar;
    }

    @Override
    public String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp) {

        if(containsWhere && !deleteOp) {
            return getQuotedName(table.schema()) + "." + getQuotedName(table.name()) + " AS " + table.alias();
        }
        else {
            return this.renderTableNameWithoutAlias(table);
        }

    }

    @Override
    public String renderTableNameWithoutAlias(DbTable table) {
        return getQuotedName(table.schema()) + "." + getQuotedName(table.name());
    }

    @Override
    public String getAliasedName(DbColumn dbColumn, boolean deleteOp) {
        if(deleteOp) {
            return dbColumn.name();
        }

        return dbColumn.tableAlias() + "."+ dbColumn.name();
    }


}
