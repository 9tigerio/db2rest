package com.homihq.db2rest.rsql.v2.parser;

import static cz.jirutka.rsql.parser.ast.RSQLOperators.*;
import static java.sql.JDBCType.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.*;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

@RequiredArgsConstructor
@Slf4j
public class MyBatisFilterVisitor implements RSQLVisitor<SqlCriterion, Object> {
    private final SqlTable sqlTable;

    @Override
    public SqlCriterion visit(AndNode node, Object optionalParameter) {
        List<AndOrCriteriaGroup> criterionList = new ArrayList<>();

        for (Node child : node.getChildren()) {
            criterionList.add(and(child.accept(this, optionalParameter)));
        }

        return processCriteriaGroup(criterionList);
    }

    @Override
    public SqlCriterion visit(OrNode node, Object optionalParameter) {
        List<AndOrCriteriaGroup> criterionList = new ArrayList<>();

        for (Node child : node.getChildren()) {
            criterionList.add(or(child.accept(this, optionalParameter)));
        }

        return processCriteriaGroup(criterionList);
    }

    @Override
    public SqlCriterion visit(ComparisonNode node, Object optionalParameter) {
        String argument = node.getArguments().get(0);
        String selector = node.getSelector();

        if (EQUAL.equals(node.getOperator())) {
            SqlColumn<Object> column = sqlTable.column(selector, INTEGER);
            if (INTEGER == column.jdbcType().orElseThrow()) {
                return ColumnAndConditionCriterion.withColumn(column).withCondition(isEqualTo(Integer.parseInt(argument))).build();
            }
            return ColumnAndConditionCriterion.withColumn(column).withCondition(isEqualTo(argument)).build();
        }
        return null;
    }

    private SqlCriterion processCriteriaGroup(List<AndOrCriteriaGroup> criterionList){
        if(criterionList.isEmpty()){
            throw new InternalError("criterionList should never be empty here");
        }

        AndOrCriteriaGroup initialCriterion = criterionList.get(0);
        List<AndOrCriteriaGroup> criterionSiblings = criterionList.subList(1, criterionList.size());
        if(!initialCriterion.subCriteria().isEmpty()){
            throw new InternalError("Unexpected subCriteria found. Initial criterion should never contain subCriteria. This is most likely a coding bug.");
        }
        return group(initialCriterion.initialCriterion().orElseThrow(), criterionSiblings);
    }
}
