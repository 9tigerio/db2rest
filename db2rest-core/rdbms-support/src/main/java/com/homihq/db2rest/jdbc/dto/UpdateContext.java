package com.homihq.db2rest.jdbc.dto;

import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class UpdateContext {

    String dbId;
    String tableName;
    DbTable table;
    String where;
    Map<String, Object> paramMap;
    List<String> updatableColumns;


    public String renderSetColumns() {
        return StringUtils.join(
                updatableColumns.stream().map(i -> i + " = " + ":set_" + i).toList(),
                ","
        );
    }

    public void createParamMap(Map<String, Object> data) {
        if (Objects.isNull(paramMap)) {
            paramMap = new HashMap<>();
        }

        for (String key : data.keySet()) {
            paramMap.put("set_" + key, data.get(key));
        }
    }
}
