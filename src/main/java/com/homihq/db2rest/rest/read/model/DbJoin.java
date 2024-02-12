package com.homihq.db2rest.rest.read.model;


import lombok.Data;

@Data
public class DbJoin {

    private String tableName;
    private String alias;
    private String joinType;

    public String render() {
        return joinType + " JOIN " + tableName + " " + alias;
    }
}
