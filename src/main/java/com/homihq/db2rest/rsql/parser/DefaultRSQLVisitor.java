package com.homihq.db2rest.rsql.parser;

import com.homihq.db2rest.rsql.operators.OperatorHandler;
import com.homihq.db2rest.rsql.operators.RSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.function.BinaryOperator;

import static org.jooq.impl.DSL.noCondition;

@Slf4j
@RequiredArgsConstructor
public class DefaultRSQLVisitor implements RSQLVisitor<Condition, Void> {

    private final Table table;


    private static final Condition NO_CONDITION = noCondition();

    @Override
    public Condition visit(AndNode andNode, Void unused) {
        System.out.println("and node - {}" + andNode);

        List<Condition> sqlConditions = getSQLConditions(andNode.getChildren());
        return joinSQLConditions(sqlConditions, Condition::and);

    }

    @Override
    public Condition visit(OrNode orNode, Void unused) {
        List<Condition> sqlConditions = getSQLConditions(orNode.getChildren());
        return joinSQLConditions(sqlConditions, Condition::or);
    }

    private List<Condition> getSQLConditions(List<Node> children) {
        return children
                .stream()
                .map(node -> node.accept(this))
                .toList();
    }

    public Condition joinSQLConditions(
            List<Condition> conditions, BinaryOperator<Condition> conditionalOperator) {
        if (conditions.stream().allMatch(NO_CONDITION::equals)) {
            return NO_CONDITION;
        }
        return conditions
                .stream()
                .reduce(NO_CONDITION, conditionalOperator);
    }



    @Override
    public Condition visit(ComparisonNode comparisonNode, Void unused) {
        ComparisonOperator op = comparisonNode.getOperator();

        Column column =
                getColumn(comparisonNode.getSelector());

        log.info("column - {}", column);

        Class type = column.getType().getTypeMappedClass();

        String queryColumnName = column.getName();

        OperatorHandler operatorHandler = RSQLOperatorHandlers.getOperatorHandler(op.getSymbol());
        if (operatorHandler == null) {
            throw new IllegalArgumentException(String.format("Operator '%s' is invalid", op.getSymbol()));
        }

        if (op.isMultiValue()) {
            return operatorHandler.handle(queryColumnName, comparisonNode.getArguments(), type);
        }
        else {
            return operatorHandler.handle(queryColumnName, comparisonNode.getArguments().get(0), type);
        }
    }

    private Column getColumn(String colName) {
        log.info("Column name - {}", colName);
        return
                table.getColumns().stream().filter(column ->
                                column.getName().equals(colName))
                        .findFirst().orElseThrow();
    }
}
