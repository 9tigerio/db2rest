package com.homihq.db2rest.rsql.operators.handler;

import com.homihq.db2rest.rsql.operators.Operator;
import com.homihq.db2rest.schema.TypeMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.ColumnAndConditionCriterion;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Slf4j
public class EqualToOperator implements Operator {

    @Override
    public SqlCriterion handle(SqlColumn<Object> column, String value, Class<?> type) {

        log.info("Type - {}", type);

        return ColumnAndConditionCriterion.withColumn(column)
                .withCondition(isEqualTo(TypeMapperUtil.parseValue(value,type))).build();

    }
}
