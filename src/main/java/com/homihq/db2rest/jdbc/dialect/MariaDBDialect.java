package com.homihq.db2rest.jdbc.dialect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.core.Dialect;
import com.homihq.db2rest.jdbc.core.model.DbTable;
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
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {

        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.getColumnDataTypeName(columnName);

                log.debug("columnDataTypeName - {}", columnDataTypeName);

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
    public String renderableTableName(DbTable table) {
        return getQuotedName(table.fullName()) + " as " + table.alias();
    }

}
