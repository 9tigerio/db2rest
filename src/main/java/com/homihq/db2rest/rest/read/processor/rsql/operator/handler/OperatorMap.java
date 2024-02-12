package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;


import com.homihq.db2rest.exception.InvalidOperatorException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OperatorMap {

    Map<String,String> opMap = new HashMap<>();



    @PostConstruct
    public void create() {
        opMap.put("==","=");
        opMap.put("=gt=",">");
        opMap.put("=gte=",">=");
        opMap.put("=lt=","<");
        opMap.put("=lte=","<=");
        opMap.put("=notnull=","IS NOT NULL");
        opMap.put("=isnull=","IS NULL");
    }

    public String getSQLOperator(String rSQLOperator) {
        return opMap.get(rSQLOperator);
    }

    public String getRSQLOperator(String expression) {
        return this.opMap.keySet()
                .stream()
                .filter(operator -> StringUtils.containsIgnoreCase(expression, operator))
                .findFirst().orElseThrow(() -> new InvalidOperatorException("Operator not supported", ""));
    }

}
