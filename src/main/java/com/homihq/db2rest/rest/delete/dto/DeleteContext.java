package com.homihq.db2rest.rest.delete.dto;

import com.homihq.db2rest.model.DbTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class DeleteContext{
    String tableName;
    DbTable table;
    String where;
    Map<String,Object> paramMap;
    Map<String,Object> data;

    public void createParamMap() {
        if(Objects.isNull(paramMap)) {
            paramMap = new HashMap<>();
        }
    }
}
