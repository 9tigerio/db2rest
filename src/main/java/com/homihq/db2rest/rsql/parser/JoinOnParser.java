package com.homihq.db2rest.rsql.parser;


import com.homihq.db2rest.exception.InvalidOperatorException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rsql.operators.RSQLOperatorHandlers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.join.JoinCondition;
import org.springframework.stereotype.Component;

import static org.mybatis.dynamic.sql.SqlBuilder.equalTo;

@RequiredArgsConstructor
@Slf4j
@Component
public class JoinOnParser  {


    public void parse(MyBatisTable rootTable, MyBatisTable childTable, String on, QueryExpressionDSL<SelectModel> queryExpressionDSL) {

        String operator = getOperator(on);
        String left = on.substring(0, on.indexOf(operator)).trim();
        String right = on.substring(on.indexOf(operator) + operator.length()).trim();

        log.info("{} | {} | {}", operator, left, right);
        //TODO Verify column and apply type
        SqlColumn<?> rootColumn = rootTable.column(left);

        queryExpressionDSL.join(childTable)
               .on(rootColumn, getJoinCondition(childTable, right, operator));


    }

    protected JoinCondition getJoinCondition(MyBatisTable childTable, String right, String operator) {

        SqlColumn<?> childColumn = childTable.column(right);
        JoinCondition joinCondition = null;
        switch (operator) {
            case "==" : joinCondition = equalTo(childColumn);

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
