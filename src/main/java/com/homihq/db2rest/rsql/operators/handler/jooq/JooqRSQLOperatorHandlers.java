package com.homihq.db2rest.rsql.operators.handler.jooq;



import java.util.HashMap;
import java.util.Map;

import static com.homihq.db2rest.rsql.operators.CustomRSQLOperators.*;

public class JooqRSQLOperatorHandlers {

    private static final Map<String, JooqOperatorHandler> OPERATOR_HANDLER_MAP = new HashMap<>();

    static {
        OPERATOR_HANDLER_MAP.put(EQUAL.getSymbol(), new JooqEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(GREATER_THAN.getSymbol(), new JooqGreaterThanOperatorHandler());

    }

    public static JooqOperatorHandler getOperatorHandler(String symbol) {
        return OPERATOR_HANDLER_MAP.get(symbol);
    }
}
