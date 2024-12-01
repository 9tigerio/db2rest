package com.homihq.db2rest.jdbc.config.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class DbJoin {

    private String tableName;
    private String alias;
    private String joinType;

    private DbColumn onLeft;
    private DbColumn onRight;
    private String onOperator;

    private List<DbJoinAndCondition> andConditions;
    private List<String> additionalWhere;

    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append(joinType)
                .append(" JOIN ")
                .append(tableName)
                .append(" ")
                .append(alias)
                .append("\n");

        if (Objects.nonNull(onLeft)) {
            builder.append(" ON ")
                    .append(onLeft.render())
                    .append(" ")
                    .append(onOperator);
        }

        if (Objects.nonNull(onRight)) {
            builder.append(" ").append(onRight.render());
        }

        if (Objects.nonNull(andConditions) && !andConditions.isEmpty()) {
            for (DbJoinAndCondition dbJoinAndCondition : andConditions) {
                builder.append("\n AND ")
                        .append(dbJoinAndCondition.leftColumn.render())
                        .append(" ")
                        .append(dbJoinAndCondition.operator)
                        .append(" ")
                        .append(dbJoinAndCondition.rightColumn.render());
            }
        }

        if (Objects.nonNull(additionalWhere) && !additionalWhere.isEmpty()) {//filters
            for (String where : additionalWhere) {
                builder.append("\n AND ").append(where);
            }
        }

        return builder.append("\n").toString();
    }

    public void addOn(DbColumn leftColumn, String operator, DbColumn rightColumn) {
        this.onRight = rightColumn;
        this.onLeft = leftColumn;
        this.onOperator = operator;
    }

    public void addAndCondition(DbColumn leftColumn, String operator, DbColumn rightColumn) {
        if (Objects.isNull(andConditions)) {
            andConditions = new ArrayList<>();
        }

        andConditions.add(new DbJoinAndCondition(leftColumn, operator, rightColumn));

    }

    public void addAdditionalWhere(String where) {
        if (Objects.isNull(additionalWhere)) {
            additionalWhere = new ArrayList<>();
        }

        additionalWhere.add(where);
    }

    private record DbJoinAndCondition(DbColumn leftColumn, String operator, DbColumn rightColumn) {
    }
}
