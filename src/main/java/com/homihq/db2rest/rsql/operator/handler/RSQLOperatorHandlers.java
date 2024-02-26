package com.homihq.db2rest.rsql.operator.handler;


import com.homihq.db2rest.rsql.operator.CustomRSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.HashMap;
import java.util.Map;

public class RSQLOperatorHandlers {

    private static final Map<String, OperatorHandler> OPERATOR_HANDLER_MAP = new HashMap<>();

    static {
        OPERATOR_HANDLER_MAP.put(RSQLOperators.EQUAL.getSymbol(), new EqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.NOT_EQUAL.getSymbol(), new NotEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.IN.getSymbol(), new InOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.NOT_IN.getSymbol(), new NotInOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.GREATER_THAN.getSymbol(), new GreaterThanOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.GREATER_THAN_OR_EQUAL.getSymbol(), new GreaterThanEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.LESS_THAN.getSymbol(), new LessThanOperatorHandler());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.LESS_THAN_OR_EQUAL.getSymbol(), new LessThanEqualToOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.LIKE.getSymbol(), new LikeOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.START_WITH.getSymbol(), new StartWithOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.END_WITH.getSymbol(), new EndWithOperatorHandler());

    }

    public static OperatorHandler getOperatorHandler(String symbol) {
        return OPERATOR_HANDLER_MAP.get(symbol);
    }
}
