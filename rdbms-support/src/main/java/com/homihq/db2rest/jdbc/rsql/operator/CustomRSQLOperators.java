package com.homihq.db2rest.jdbc.rsql.operator;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

import java.util.Arrays;
import java.util.Set;

public class CustomRSQLOperators extends RSQLOperators {

    public static final ComparisonOperator LIKE = new ComparisonOperator("=like=", false);
    public static final ComparisonOperator START_WITH = new ComparisonOperator("=startWith=", false);
    public static final ComparisonOperator END_WITH = new ComparisonOperator("=endWith=", false);

    public static final ComparisonOperator IS_NULL = new ComparisonOperator(
            new String[]{"=isnull=", "=null=", "=na="}
            , false);

    public static final ComparisonOperator JSONB_CONTAIN = new ComparisonOperator(
            new String[]{"=jsonbContain=", "=jbc="}
            , false);

    public static final ComparisonOperator JSON_CONTAIN = new ComparisonOperator(
            new String[]{"=jsonContain=", "=jc="}
            , false);

    public static final ComparisonOperator JSONB_EQUAL = new ComparisonOperator(
            new String[]{"=jbe="}
            , false);

    public static final ComparisonOperator JSONB_KEY_EXISTS = new ComparisonOperator(
            new String[]{"=jbKeyExist="}
            , false);

    public static final ComparisonOperator JSON_CONTAINS_IN_ARRAY = new ComparisonOperator(
            new String[]{"=jcInArray="}
            , false);

    public static final ComparisonOperator NOT_LIKE = new ComparisonOperator(
            new String[]{"=notlike=","=nk="}
            , false);

    public static Set<ComparisonOperator> customOperators() {
        Set<ComparisonOperator> comparisonOperators = RSQLOperators.defaultOperators();
        comparisonOperators.addAll(Arrays.asList(LIKE, START_WITH, END_WITH, IS_NULL,
                JSONB_CONTAIN,JSON_CONTAIN,JSONB_EQUAL,JSONB_KEY_EXISTS, JSON_CONTAINS_IN_ARRAY,NOT_LIKE));
        return comparisonOperators;
    }

}
