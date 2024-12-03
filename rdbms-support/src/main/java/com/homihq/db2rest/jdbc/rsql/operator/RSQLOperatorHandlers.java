package com.homihq.db2rest.jdbc.rsql.operator;


import com.homihq.db2rest.jdbc.rsql.operator.handler.*;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
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

        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.IS_NULL.getSymbol(), new IsNullOperatorHandler());

        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.JSONB_CONTAIN.getSymbol(), new JsonbContainOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.JSON_CONTAIN.getSymbol(), new JsonContainOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.JSONB_EQUAL.getSymbol(), new JsonbEqualToOperatorHandler());

        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.JSONB_KEY_EXISTS.getSymbol(), new JsonbKeyExistsOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.JSON_CONTAINS_IN_ARRAY.getSymbol(), new JsonContainInArrayOperatorHandler());

        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.NOT_LIKE.getSymbol(), new NotLikeOperatorHandler());
        OPERATOR_HANDLER_MAP.put(CustomRSQLOperators.IS_NOT_NULL.getSymbol(), new IsNotNullOperatorHandler());

    }

    public OperatorHandler getOperatorHandler(String symbol) {
        return OPERATOR_HANDLER_MAP.get(symbol);
    }
}
