package com.homihq.db2rest.rsql.operators;

import com.homihq.db2rest.rsql.operators.handler.*;

import java.util.HashMap;
import java.util.Map;

import static com.homihq.db2rest.rsql.operators.CustomRSQLOperators.*;

public class RSQLOperatorHandlers {

    private static final Map<String, OperatorHandler> OPERATOR_HANDLER_MAP = new HashMap<>();

    static {
        OPERATOR_HANDLER_MAP.put(EQUAL.getSymbol(), new EqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(GREATER_THAN.getSymbol(), new GreaterThanOperatorHandler());
        OPERATOR_HANDLER_MAP.put(GREATER_THAN_OR_EQUAL.getSymbol(), new GreaterThanEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(LESS_THAN.getSymbol(), new LessThanOperatorHandler());
        OPERATOR_HANDLER_MAP.put(LESS_THAN_OR_EQUAL.getSymbol(), new LessThanEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(NOT_EQUAL.getSymbol(), new NotEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(LIKE.getSymbol(), new LikeOperatorHandler());
        OPERATOR_HANDLER_MAP.put(START_WITH.getSymbol(), new EndWithOperatorHandler());
        OPERATOR_HANDLER_MAP.put(END_WITH.getSymbol(), new EndWithOperatorHandler());
    }

    public static OperatorHandler getOperatorHandler(String symbol) {
        return OPERATOR_HANDLER_MAP.get(symbol);
    }
}
