package com.homihq.db2rest.rest.read.processor.rsql.operator.handler;


import jakarta.annotation.PostConstruct;
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
    }

    public String getSQLOp(String rsqlOp) {
        return opMap.get(rsqlOp);
    }

}
