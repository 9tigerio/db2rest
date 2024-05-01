package com.homihq.db2rest.jdbc.config.rsql.operator.handler;


import com.homihq.db2rest.core.exception.InvalidOperatorException;
import org.apache.commons.lang3.StringUtils;
import java.util.Map;

public class OperatorMap {

    static Map<String,String> opMap = Map.of(
            "==","=",
            "=gt=",">",
            "=gte=",">=",
            "=lt=","<",
            "=lte=","<=",
            "=notnull=","IS NOT NULL",
            "=isnull=","IS NULL");


    public static String getSQLOperator(String rSQLOperator) {
        return opMap.get(rSQLOperator);
    }

    public static String getRSQLOperator(String expression) {
        return opMap.keySet()
                .stream()
                .filter(operator -> StringUtils.containsIgnoreCase(expression, operator))
                .findFirst().orElseThrow(() -> new InvalidOperatorException("Operator not supported", ""));
    }

}
