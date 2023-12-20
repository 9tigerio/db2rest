package com.homihq.db2rest.rsql.operators.handler;


import static com.homihq.db2rest.rsql.operators.CustomRSQLOperators.*;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.*;


import java.util.HashMap;
import java.util.Map;

public class RSQLOperatorHandlers {

    private static final Map<String, OperatorHandler> OPERATOR_HANDLER_MAP = new HashMap<>();

    static {





    }

    public static OperatorHandler getOperatorHandler(String symbol) {
        return OPERATOR_HANDLER_MAP.get(symbol);
    }
}
