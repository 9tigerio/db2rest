package com.homihq.db2rest.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.convert.StringConvert;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;
import schemacrawler.schema.DatabaseInfo;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostGreSQLDialect implements Dialect{

    private final ObjectMapper objectMapper;

    @Override
    public boolean canSupport(DatabaseInfo databaseInfo) {
        return StringUtils.equalsAnyIgnoreCase(databaseInfo.getDatabaseProductName(), "POSTGRESQL");
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {
        try {
            for (String columnName : insertableColumns) {
                Object value = data.get(columnName);

                String columnDataTypeName = table.lookupColumn(columnName).getColumnDataType().getName();

                log.info("columnDataTypeName - {}", columnDataTypeName);
                if(Objects.isNull(value)) continue;

                if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "json")) {
                    Object v = convertToJson(value, columnDataTypeName);
                    data.put(columnName, v);
                }
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamp")) {
                    LocalDateTime v = convertToLocalDateTime((String)value);
                    data.put(columnName, v);

                }
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamptz")) {
                    OffsetDateTime v = convertToOffsetDateTime((String)value);
                    data.put(columnName, v);

                }
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timestamptz")) {
                    OffsetDateTime v = convertToOffsetDateTime((String)value);
                    data.put(columnName, v);
                }
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "timetz")) {
                        OffsetTime v = convertToOffsetTime((String)value);
                        data.put(columnName, v);
                }
                else if (StringUtils.equalsAnyIgnoreCase(columnDataTypeName, "int4","int2","int8","int")) {
                    data.put(columnName, Integer.valueOf(value.toString().trim()));
                }

            }
        }
        catch (Exception exception) {
            throw new GenericDataAccessException(exception.getMessage());
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
        if(value.getClass().isAssignableFrom(Integer.class)) {
            return value;
        }
        else{
            return StringConvert.INSTANCE.convertFromString(Integer.class, value.toString());
        }
    }

    private Object convertToJson(Object value, String columnDataTypeName) throws Exception {
        PGobject pGobject = new PGobject();
        pGobject.setType(columnDataTypeName);
        pGobject.setValue(objectMapper.writeValueAsString(value));

        return pGobject;
    }


}
