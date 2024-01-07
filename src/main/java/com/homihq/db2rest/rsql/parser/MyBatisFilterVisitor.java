package com.homihq.db2rest.rsql.parser;


import static org.mybatis.dynamic.sql.SqlBuilder.*;
import java.util.ArrayList;
import java.util.List;

import com.homihq.db2rest.rsql.operators.OperatorV2;
import com.homihq.db2rest.rsql.operators.RSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.*;


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
    public SqlCriterion visit(ComparisonNode comparisonNode, Object optionalParameter) {
        ComparisonOperator op = comparisonNode.getOperator();
        String columnName = comparisonNode.getSelector();

        OperatorV2 operatorHandler = RSQLOperatorHandlers.getOperatorHandler(op.getSymbol());
        if (operatorHandler == null) {
            throw new IllegalArgumentException(String.format("Operator '%s' is invalid", op.getSymbol()));
        }

        SqlColumn<Object> column = sqlTable.column(columnName);

        //Dummy type
        Class clazz = Integer.class;


        if (op.isMultiValue()) {

            return operatorHandler.handle(column, comparisonNode.getArguments(), clazz);
        }
        else {
            return operatorHandler.handle(column, comparisonNode.getArguments().get(0), clazz);
        }

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
