package com.homihq.db2rest.jdbc.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;


public record JoinDetail (
        String schemaName,
        String table, String withTable, List<String> fields,
                          List<String> on, String filter, String joinType){

    public String getJoinType() {
        return StringUtils.isBlank(joinType) ? "INNER" :
                StringUtils.upperCase(joinType);

    }
    public boolean hasWith() {
        return StringUtils.isNotBlank(withTable);
    }
    public boolean hasOn() {
        return Objects.nonNull(on) && !on.isEmpty();
    }

    public boolean hasFilter() {
        return StringUtils.isNotBlank(filter);
    }
}
