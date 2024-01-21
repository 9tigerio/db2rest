package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.Operator;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import java.util.Arrays;
import java.util.List;

import static org.mybatis.dynamic.sql.SqlBuilder.isNotIn;

public class NotInOperator implements Operator {

    @Override
    public SqlCriterion handle(SqlColumn<Object> column, String value, Class type) {
       return handle(column, Arrays.asList(value), type);

    }

    @Override
    public SqlCriterion handle(SqlColumn<Object> column, List<String> values, Class type) {
        Object [] v =
                values.stream().map(value -> parseValue(value, type)).toList().toArray();

        return ColumnAndConditionCriterion.withColumn(column)
                .withCondition(isNotIn(v)).build();
    }

}
