package com.homihq.db2rest.rest.read.v2.dto;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.select.join.JoinCriterion;
import org.mybatis.dynamic.sql.select.join.JoinType;

import java.util.List;


public record JoinDetail (String table, List<String> fields,String on, List<String> andFilter, String type){

    public JoinType getJoinType() {
        return StringUtils.isBlank(type) ? JoinType.INNER :
                JoinType.valueOf(StringUtils.upperCase(type));

    }


}
