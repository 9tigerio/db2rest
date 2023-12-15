package com.homihq.db2rest.rsql;

import com.homihq.db2rest.rsql.operators.handler.OperatorHandler;
import com.homihq.db2rest.rsql.operators.handler.RSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultRSQLVisitor implements RSQLVisitor<String, Object> {

    private final Table table;

    @Override
    public String visit(AndNode andNode, Object o) {
        return "( " + andNode.getChildren().stream().map(node -> node.accept(this))
                .collect(Collectors.joining(" AND ")) + " ) ";
    }

    @Override
    public String visit(OrNode orNode, Object o) {
        return "( " +
                orNode.getChildren().stream().map(node -> node.accept(this)).collect(Collectors.joining(" OR ")) +
                " ) ";
    }

    @Override
    public String visit(ComparisonNode comparisonNode, Object o) {
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
