package com.homihq.db2rest.rsql.v1.parser;


import com.homihq.db2rest.exception.InvalidOperatorException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rsql.v1.operators.RSQLOperatorHandlers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.join.EqualTo;
import org.mybatis.dynamic.sql.select.join.JoinCondition;
import org.springframework.stereotype.Component;

import java.util.Objects;


@RequiredArgsConstructor
@Slf4j
@Component
public class JoinOnParser  {


    public void parse(MyBatisTable rootTable, MyBatisTable childTable, JoinDetail joinDetail,  QueryExpressionDSL<SelectModel> queryExpressionDSL) {

        String operator = getOperator(joinDetail.on());
        String left = joinDetail.on().substring(0, joinDetail.on().indexOf(operator)).trim();
        String right = joinDetail.on().substring(joinDetail.on().indexOf(operator) + operator.length()).trim();

        log.info("{} | {} | {}", operator, left, right);
        //TODO Verify column and apply type
        SqlColumn<?> rootColumn = rootTable.column(left);

        QueryExpressionDSL<SelectModel>.JoinSpecificationFinisher joinSpecificationFinisher =
        queryExpressionDSL.join(childTable)
               .on(rootColumn, getJoinCondition(childTable, right, operator));


        if(Objects.nonNull(joinDetail.andFilters()) && !joinDetail.andFilters().isEmpty()) {
            for(String filter : joinDetail.andFilters()) {
                operator = getOperator(filter);
                left = filter.substring(0, joinDetail.on().indexOf(operator)).trim();
                right = filter.substring(joinDetail.on().indexOf(operator) + operator.length()).trim();

                //joinSpecificationFinisher.and(childTable.column(left))
            }
        }

    }



    protected JoinCondition getJoinCondition(MyBatisTable childTable, String right, String operator) {

        SqlColumn<?> childColumn = childTable.column(right);
        JoinCondition joinCondition = null;
        switch (operator) {
            case "==" : joinCondition = new EqualTo(childColumn);

        }

        return joinCondition;
    }

    protected String getOperator(String on) {
        return
        RSQLOperatorHandlers.getAllOperators()
                .stream()
                .filter(op -> StringUtils.containsIgnoreCase(on, op))
                .findFirst().orElseThrow(() -> new InvalidOperatorException("Operator not supported", ""));


    }

}
