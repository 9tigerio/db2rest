package com.homihq.db2rest.mongo.rsql.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Arrays;

public enum Operator {
    EQUAL(RSQLOperators.EQUAL),
    NOT_EQUAL(RSQLOperators.NOT_EQUAL),
    GREATER_THAN(RSQLOperators.GREATER_THAN),
    GREATER_THAN_OR_EQUAL(RSQLOperators.GREATER_THAN_OR_EQUAL),
    LESS_THAN(RSQLOperators.LESS_THAN),
    LESS_THAN_OR_EQUAL(RSQLOperators.LESS_THAN_OR_EQUAL),
    IN(RSQLOperators.IN),
    NOT_IN(RSQLOperators.NOT_IN),
    REGEX(new ComparisonOperator("=re=", false)),
    EXISTS(new ComparisonOperator("=ex=", false));

    public final ComparisonOperator comparisonOperator;

    Operator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public static Operator toOperator(ComparisonOperator operator) {
        return Arrays.stream(values())
                .filter(value -> value.comparisonOperator.equals(operator))
                .findFirst()
                .orElse(null);
    }

}