package com.homihq.db2rest.rsql.parser;

import com.homihq.db2rest.rsql.operators.handler.OperatorHandler;
import com.homihq.db2rest.rsql.operators.handler.RSQLOperatorHandlers;
import com.homihq.db2rest.rsql.operators.handler.jooq.JooqOperatorHandler;
import com.homihq.db2rest.rsql.operators.handler.jooq.JooqRSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Condition;
import org.jooq.Query;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@Slf4j
@RequiredArgsConstructor
public class JOOQRSQLVisitor implements RSQLVisitor<Condition, Void> {

    private final Table table;
    private final Query query;


    @Override
    public Condition visit(AndNode andNode, Void unused) {
        System.out.println("and node - {}" + andNode);
        return null;
    }

    @Override
    public Condition visit(OrNode orNode, Void unused) {
        System.out.println("or node - {}" + orNode);
        return null;
    }

    @Override
    public Condition visit(ComparisonNode comparisonNode, Void unused) {
        ComparisonOperator op = comparisonNode.getOperator();

        Column column =
                getColumn(comparisonNode.getSelector());

        log.info("column - {}", column);

        Class type = column.getType().getTypeMappedClass();

        String queryColumnName = column.getName();

        JooqOperatorHandler operatorHandler = JooqRSQLOperatorHandlers.getOperatorHandler(op.getSymbol());
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
