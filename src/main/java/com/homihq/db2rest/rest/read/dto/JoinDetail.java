package com.homihq.db2rest.rest.read.dto;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public record JoinDetail (String table, String with, List<String> fields,
                          List<String> on, List<String> andFilters, String type){

    public String getJoinType() {
        return StringUtils.isBlank(type) ? "INNER" :
                StringUtils.upperCase(type);

    }


}
