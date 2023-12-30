package com.homihq.db2rest.rest.query.model;

import lombok.Data;

@Data
public class RJoin {

    String schemaName;
    String tableName;
    String alias;

    String left;
    String right;
    String rightTable;
    String rightTableAlias;

    String type;

    public String getJoin() {

        return type + " JOIN " +  tableName + " " + alias +
            " ON " + alias + "." +   right + " = " + rightTableAlias + "." + right;
    }

    /*
    private String process(String feelsLike, String temperature, String unit) {
        return STR
                . "Today's weather is \{ feelsLike }, with a temperature of \{ temperature } degrees \{ unit }" ;
    }

     */
}
