package com.homihq.db2rest.rest.read.model;


import lombok.Data;

import java.util.Objects;

@Data
public class DbJoin {

    private String tableName;
    private String alias;
    private String joinType;

    private DbColumn onLeft;
    private DbColumn onRight;
    private String onOperator;

    public String render() {

        String str = joinType + " JOIN " + tableName + " " + alias + "\n";

        if(Objects.nonNull(onLeft)) {
            str += " ON " + onLeft.render() + " " + onOperator + " " + onRight.render();
        }
        return str;
    }

    public void addOn(DbColumn leftColumn, String operator, DbColumn rightColumn) {
        this.onRight = rightColumn;
        this.onLeft = leftColumn;
        this.onOperator = operator;
    }
}
