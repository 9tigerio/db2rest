package com.homihq.db2rest.jdbc.config.model;


import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public record DbWhere(String tableName, DbTable table, List<DbColumn> columns, Map<String,Object> paramMap, String op) {

    public boolean isDelete(){
        return StringUtils.equalsIgnoreCase(op, "delete");
    }
}
