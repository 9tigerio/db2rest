package com.homihq.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.convert.StringConvert;
import org.postgresql.util.PGobject;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class PostGreSQLDialect implements Dialect {

    private final ObjectMapper objectMapper;

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {

        for (String columnName : insertableColumns) {
            Object value = data.get(columnName);

            String columnDataTypeName = table.getColumnDataTypeName(columnName);

            log.info("columnName : {} || columnDataTypeName - {}", columnName, columnDataTypeName);
            if (Objects.isNull(value)) continue;

            if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {
                Object v = convertToJson(value, columnDataTypeName);
                data.put(columnName, v);
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamp")) {
                LocalDateTime v = convertToLocalDateTime((String) value);
                data.put(columnName, v);

            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamptz")) {
                OffsetDateTime v = convertToOffsetDateTime((String) value);
                data.put(columnName, v);

            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamptz")) {
                OffsetDateTime v = convertToOffsetDateTime((String) value);
                data.put(columnName, v);
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timetz")) {
                OffsetTime v = convertToOffsetTime((String) value);
                data.put(columnName, v);
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "int4", "int2", "int8", "int")) {
                data.put(columnName, Long.valueOf(value.toString().trim()));
            } else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "numeric")) {
                data.put(columnName, Double.valueOf(value.toString().trim()));
            }
            else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "year")) {
                data.put(columnName, Integer.valueOf(value.toString().trim()));
            }

        }

    }

    private OffsetTime convertToOffsetTime(String value) {
        return OffsetTime.parse(value, DateTimeFormatter.ISO_OFFSET_TIME);
    }

    private LocalDateTime convertToLocalDateTime(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
    }

    private OffsetDateTime convertToOffsetDateTime(String value) {
        return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private Object convertToInt(Object value) {
        if (value.getClass().isAssignableFrom(Integer.class)) {
            return value;
        } else {
            return StringConvert.INSTANCE.convertFromString(Integer.class, value.toString());
        }
    }

    private Object convertToJson(Object value, String columnDataTypeName) {
        try {
            PGobject pGobject = new PGobject();
            pGobject.setType(columnDataTypeName);
            pGobject.setValue(objectMapper.writeValueAsString(value));

            return pGobject;
        } catch (Exception e) {
            throw new GenericDataAccessException(e.getLocalizedMessage());
        }
    }


}
