package com.homihq.db2rest.rest.query.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RCondition {

    String columnName;
    String operator;

    Object [] values;

    RCondition lCondition;
    RCondition rCondition;

    public RCondition or(RCondition rCondition) {
        return RCondition.builder()
                .rCondition(rCondition)
                .operator("OR")
                .build();
    }

    public RCondition and(RCondition rCondition) {
        return RCondition.builder()
                .rCondition(rCondition)
                .operator("AND")
                .build();
    }

    public static RCondition noCondition() {
        return RCondition.builder()
                .operator("NONE")
                .build();
    }
}
