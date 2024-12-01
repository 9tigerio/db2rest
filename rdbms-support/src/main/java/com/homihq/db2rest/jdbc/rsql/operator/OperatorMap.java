package com.homihq.db2rest.jdbc.rsql.operator;

import com.homihq.db2rest.core.exception.InvalidOperatorException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@UtilityClass
public class OperatorMap {

    private static final Map<String, String> OP_MAP = Map.of(
            "==", "=",
            "=gt=", ">",
            "=gte=", ">=",
            "=lt=", "<",
            "=lte=", "<=",
            "=notnull=", "IS NOT NULL",
            "=isnull=", "IS NULL");


    public String getSQLOperator(String rSQLOperator) {
        return OP_MAP.get(rSQLOperator);
    }

    public String getRSQLOperator(String expression) {
        return OP_MAP.keySet()
                .stream()
                .filter(operator -> StringUtils.containsIgnoreCase(expression, operator))
                .findFirst().orElseThrow(() -> new InvalidOperatorException("Operator not supported", ""));
    }

}
