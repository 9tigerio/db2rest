package com.homihq.db2rest.rsql.v1.operators;

import com.homihq.db2rest.rsql.v1.operators.handler.*;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@Slf4j
public class RSQLOperatorHandlers {

    private static final Map<String, Operator> OPERATOR_HANDLER_MAP = new HashMap<>();

    static {
        OPERATOR_HANDLER_MAP.put(RSQLOperators.EQUAL.getSymbol(), new EqualToOperator());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.GREATER_THAN.getSymbol(), new GreaterThanOperator());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.GREATER_THAN_OR_EQUAL.getSymbol(), new GreaterThanEqualToOperator());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.LESS_THAN.getSymbol(), new LessThanOperator());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.LESS_THAN_OR_EQUAL.getSymbol(), new LessThanEqualToOperator());
        OPERATOR_HANDLER_MAP.put(RSQLOperators.NOT_EQUAL.getSymbol(), new NotEqualToOperator());
        OPERATOR_HANDLER_MAP.put(SimpleRSQLOperators.LIKE.getSymbol(), new LikeOperator());
        OPERATOR_HANDLER_MAP.put(SimpleRSQLOperators.END_WITH.getSymbol(), new EndWithOperator());
    }

    public static Operator getOperatorHandler(String symbol) {

        log.info("OPERATOR_HANDLER_MAP - {}", OPERATOR_HANDLER_MAP);

        return OPERATOR_HANDLER_MAP.get(symbol);
    }

    public static Set<String> getAllOperators() {
        return OPERATOR_HANDLER_MAP.keySet();
    }
}
