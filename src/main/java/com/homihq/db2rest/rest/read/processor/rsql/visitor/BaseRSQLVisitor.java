package com.homihq.db2rest.rest.read.processor.rsql.visitor;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbWhere;
import com.homihq.db2rest.rest.read.processor.rsql.operator.handler.OperatorHandler;
import com.homihq.db2rest.rest.read.processor.rsql.operator.handler.RSQLOperatorHandlers;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class BaseRSQLVisitor implements RSQLVisitor<String, Object> {

    private final DbWhere dbWhere;

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
    public String visit(ComparisonNode node, Object o) {
        ComparisonOperator op = node.getOperator();

        DbColumn sqlColumn = getColumn(node.getSelector());

        if (sqlColumn == null) {
            throw new IllegalArgumentException(String.format("Field '%s' is invalid", node.getSelector()));
        }

        Class type = sqlColumn.column().getType().getTypeMappedClass();

        String queryColumnName = sqlColumn.name();

        OperatorHandler operatorHandler = RSQLOperatorHandlers.getOperatorHandler(op.getSymbol());
        if (operatorHandler == null) {
            throw new IllegalArgumentException(String.format("Operator '%s' is invalid", op.getSymbol()));
        }

        if (op.isMultiValue()) {
            return operatorHandler.handle(queryColumnName, node.getArguments(), type, this.dbWhere.paramMap());
        }
        else {
            return operatorHandler.handle(queryColumnName, node.getArguments().get(0), type, this.dbWhere.paramMap());
        }

    }


    public DbColumn getColumn(String col) {
        return
        this.dbWhere.columns()
                .stream()
                .filter(dbColumn ->
                    StringUtils.equalsIgnoreCase(this.dbWhere.tableName(), dbColumn.tableName())
                    &&
                    StringUtils.equalsIgnoreCase(dbColumn.name(), col)
                ).findFirst()
                .orElseThrow(() -> new InvalidColumnException(this.dbWhere.tableName(), col));
    }

}
