package com.homihq.db2rest.rest.create.param;

import com.homihq.db2rest.model.DbColumn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ParameterValueMapCreator {

    public Map<String,Object> prepareValues(List<DbColumn> dbColumns, Map<String, Object> data, String tsId) {
        Map<String, Object> values = new HashMap<>();

        for(DbColumn dbColumn : dbColumns) {
            setValue(data, dbColumn, values, tsId);
        }

        return values;
    }

    private static void setValue(Map<String, Object> data, DbColumn dbColumn, Map<String, Object> values, String tsId) {
        if(!dbColumn.isPk() || StringUtils.isNotBlank(tsId)) {
            String columnName = dbColumn.name();
            Object value = data.get(columnName);

            if(Objects.nonNull(value)) {
                values.put(columnName, value);
            }
            else if(dbColumn.hasDefaultValue() && !dbColumn.isDateTimeFamily()){
                values.put(columnName, dbColumn.getDefaultValue());
            }

        }
    }

    public Map<String,Object> prepareValues(List<DbColumn> dbColumns, Map<String, Object> data, List<String> columns, String tsId) {
        Map<String, Object> values = new HashMap<>();

        log.info("Columns - {}" , columns);

        for(DbColumn dbColumn : dbColumns) {
            String columnName = dbColumn.name();

            if(columns.contains(columnName)) {
                setValue(data, dbColumn, values,tsId);
            }

        }

        return values;
    }

}
