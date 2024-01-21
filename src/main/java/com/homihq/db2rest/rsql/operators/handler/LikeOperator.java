package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.Operator;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import static org.mybatis.dynamic.sql.SqlBuilder.isLessThan;
import static org.mybatis.dynamic.sql.SqlBuilder.isLike;


public class LikeOperator implements Operator {

   private static final String OPERATOR = " like ";

    @Override
    public SqlCriterion handle(SqlColumn<Object> column, String value, Class type) {

        return ColumnAndConditionCriterion.withColumn(column)
                .withCondition(isLike(value)).build();
    }

}
