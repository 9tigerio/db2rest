package com.homihq.db2rest.d1;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
public class D1Dialect implements Dialect {

    @Override
    public boolean supportAlias() {
        return false;
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        for (String columnName : insertableColumns) {
            Object value = data.get(columnName);

            String columnDataTypeName = table.getColumnDataTypeName(columnName);

            log.debug("columnName : {} || columnDataTypeName - {}", columnName, columnDataTypeName);
            if (Objects.isNull(value)) continue;

            if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "text")) {
                data.put(columnName, value);
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "integer")) {
                data.put(columnName, Long.valueOf(value.toString().trim()));
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "real")) {
                data.put(columnName, Double.valueOf(value.toString().trim()));
            }
            else  {
                throw new GenericDataAccessException("Unknown data type " + columnDataTypeName + " for " + columnName);
            }

        }
    }
}
