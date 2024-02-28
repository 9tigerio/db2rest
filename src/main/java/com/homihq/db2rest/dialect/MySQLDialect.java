package com.homihq.db2rest.dialect;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.DatabaseInfo;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MySQLDialect implements Dialect{

    private final ObjectMapper objectMapper;

    @Override
    public boolean canSupport(String getDbProductName) {
        return StringUtils.equalsAnyIgnoreCase(getDbProductName, "MYSQL");
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {

        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.getColumnDataTypeName(columnName);

                log.info("columnDataTypeName - {}", columnDataTypeName);

                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {

                    data.put(columnName, objectMapper.writeValueAsString(value));
                }

            }
        }
        catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
        }

    }

}
