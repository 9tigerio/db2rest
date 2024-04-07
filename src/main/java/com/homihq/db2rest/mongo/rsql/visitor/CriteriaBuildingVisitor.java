package com.homihq.db2rest.mongo.rsql.visitor;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.NoArgRSQLVisitorAdapter;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class CriteriaBuildingVisitor extends NoArgRSQLVisitorAdapter<Criteria> {

    private final ComparisonToCriteriaConverter converter;
    private final Class<?> targetEntityType;

    @Override
    public Criteria visit(AndNode node) {
        Criteria parent = new Criteria();
        List<Criteria> children = getChildCriteria(node);
        return parent.andOperator(children.toArray(new Criteria[0]));
    }

    @Override
    public Criteria visit(OrNode node) {
        Criteria parent = new Criteria();
        List<Criteria> children = getChildCriteria(node);
        return parent.orOperator(children.toArray(new Criteria[0]));
    }

    @Override
    public Criteria visit(ComparisonNode node) {
        return converter.asCriteria(node, targetEntityType);
    }

    private List<Criteria> getChildCriteria(LogicalNode node) {
        return node.getChildren().stream()
                .map(this::visit)
                .toList();
    }

    private Criteria visit(Node node) {
        if (node instanceof AndNode andNode) {
            return visit(andNode);
        } else if (node instanceof OrNode orNode) {
            return visit(orNode);
        } else {
            return visit((ComparisonNode) node);
        }
    }
}