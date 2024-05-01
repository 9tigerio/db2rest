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

        String str = joinType + " JOIN " + tableName + " " + alias + "\n";

        if(Objects.nonNull(onLeft)) {
            str += " ON " + onLeft.render() + " " + onOperator + " " + onRight.render();
        }

        if(Objects.nonNull(andConditions) && !andConditions.isEmpty()) {
            for(DbJoinAndCondition dbJoinAndCondition : andConditions) {
                str += "\n AND " + dbJoinAndCondition.leftColumn.render() + " " + dbJoinAndCondition.operator + " "
                        + dbJoinAndCondition.rightColumn.render();
            }
        }

        if(Objects.nonNull(additionalWhere) && !additionalWhere.isEmpty()) {//filters
            for(String where : additionalWhere) {
                str += "\n AND " + where;
            }
        }

        return str + " \n ";
    }

    public void addOn(DbColumn leftColumn, String operator, DbColumn rightColumn) {
        this.onRight = rightColumn;
        this.onLeft = leftColumn;
        this.onOperator = operator;
    }

    public void addAndCondition(DbColumn leftColumn, String operator, DbColumn rightColumn) {
        if(Objects.isNull(andConditions)) andConditions = new ArrayList<>();

        andConditions.add(new DbJoinAndCondition(leftColumn, operator, rightColumn));

    }

    public void addAdditionalWhere(String where) {
        if(Objects.isNull(additionalWhere)) additionalWhere = new ArrayList<>();

        additionalWhere.add(where);
    }

    private record DbJoinAndCondition(DbColumn leftColumn, String operator, DbColumn rightColumn) {}
}
