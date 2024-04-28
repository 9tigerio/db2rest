package com.homihq.db2rest.mongo.rsql;

import com.homihq.db2rest.mongo.rsql.operator.Operator;
import com.homihq.db2rest.mongo.rsql.visitor.ComparisonToCriteriaConverter;
import com.homihq.db2rest.mongo.rsql.visitor.CriteriaBuildingVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Arrays;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class RsqlMongodbAdapter {

    private final ComparisonToCriteriaConverter converter;

    public Criteria getCriteria(String rsql, Class<?> targetEntityType) {
        Node node = getRSQLParser().parse(rsql);
        var visitor = new CriteriaBuildingVisitor(converter, targetEntityType);
        return node.accept(visitor);
    }

    private RSQLParser getRSQLParser() {
        var comparisonOperators = Arrays.stream(Operator.values())
                .map(op -> op.comparisonOperator)
                .collect(Collectors.toSet());
        return new RSQLParser(comparisonOperators);
    }

}
