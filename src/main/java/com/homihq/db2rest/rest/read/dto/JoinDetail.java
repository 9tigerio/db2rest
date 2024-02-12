package com.homihq.db2rest.rest.read.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;


public record JoinDetail (String table, String with, List<String> fields,
                          List<String> on, List<String> andFilters, String type){

    public String getJoinType() {
        return StringUtils.isBlank(type) ? "INNER" :
                StringUtils.upperCase(type);

    }
    public boolean hasOn() {
        return Objects.nonNull(on) && !on.isEmpty();
    }

}
