package com.homihq.db2rest.jdbc.rsql.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Arrays;
import java.util.Set;

public class CustomRSQLOperators extends RSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=like=", false);
    public static final ComparisonOperator START_WITH = new ComparisonOperator("=startWith=", false);
    public static final ComparisonOperator END_WITH = new ComparisonOperator("=endWith=", false);

    public static Set<ComparisonOperator> customOperators() {
        Set<ComparisonOperator> comparisonOperators = RSQLOperators.defaultOperators();
        comparisonOperators.addAll(Arrays.asList(LIKE, START_WITH, END_WITH));
        return comparisonOperators;
    }

}
