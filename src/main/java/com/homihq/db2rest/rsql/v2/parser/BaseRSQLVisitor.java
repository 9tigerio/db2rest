package com.homihq.db2rest.rsql.v2.parser;

import com.homihq.db2rest.rest.query.model.RCondition;
import com.homihq.db2rest.rsql.v2.operators.Operator;
import com.homihq.db2rest.rsql.v2.operators.RSQLOperatorHandlers;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.ast.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.BinaryOperator;


@Slf4j
@RequiredArgsConstructor
public class BaseRSQLVisitor implements RSQLVisitor<RCondition, Void> {

    private final SchemaManager schemaManager;
    //private final QueryBuilderContext context;
    private final String table;
    private final String schema;
    private static final RCondition NO_CONDITION = RCondition.noCondition();

    @Override
    public RCondition visit(AndNode andNode, Void unused) {
        log.info("AND");
        List<RCondition> sqlConditions = getSQLConditions(andNode.getChildren());
        log.info("# sqlConditions -- {}", sqlConditions);

        return joinSQLConditions(sqlConditions, RCondition::and);

    }

    @Override
    public RCondition visit(OrNode orNode, Void unused) {
        log.info("OR");
        List<RCondition> sqlConditions = getSQLConditions(orNode.getChildren());
        log.info("sqlConditions -- {}", sqlConditions);
        return joinSQLConditions(sqlConditions, RCondition::or);
    }

    private List<RCondition> getSQLConditions(List<Node> children) {
        return children
                .stream()
                .map(node -> node.accept(this))
                .toList();
    }

    public RCondition joinSQLConditions(
            List<RCondition> conditions, BinaryOperator<RCondition> conditionalOperator) {
        if (conditions.stream().allMatch(NO_CONDITION::equals)) {
            return NO_CONDITION;
        }
        return conditions
                .stream()
                .reduce(NO_CONDITION, conditionalOperator);
    }


    @Override
    public RCondition visit(ComparisonNode comparisonNode, Void unused) {
        ComparisonOperator op = comparisonNode.getOperator();
        String columnName = comparisonNode.getSelector();
        String tableName = table;
        String [] tabCol = columnName.split("\\.");

        if(tabCol.length == 2) {
            tableName = tabCol[0];
            columnName = tabCol[1];
        }

        //Column column = this.schemaManager.getColumn(schema, tableName, columnName);


        //Class type = column.getColumnDataType().getTypeMappedClass();
        Class type = String.class;

        //String queryColumnName = context.getTableAlias(tableName) + "." + columnName;

        Operator operator = RSQLOperatorHandlers.getOperatorHandler(op.getSymbol());

        if (operator == null) {
            throw new IllegalArgumentException(String.format("Operator '%s' is invalid", op.getSymbol()));
        }

        if (op.isMultiValue()) {
            return operator.handle(columnName, comparisonNode.getArguments(), type);
        }
        else {
            return operator.handle(columnName, comparisonNode.getArguments().get(0), type);
        }
    }

}
