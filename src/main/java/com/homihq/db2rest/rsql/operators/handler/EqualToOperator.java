package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.OperatorV2;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Slf4j
public class EqualToOperator implements OperatorV2 {

    @Override
    public SqlCriterion handle(SqlColumn<Object> column, String value, Class type) {
        return ColumnAndConditionCriterion.withColumn(column)
                .withCondition(isEqualTo(value)).build();

    }
}
