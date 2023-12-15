package com.homihq.db2rest.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.RequiredArgsConstructor;
import schemacrawler.schema.Table;

@RequiredArgsConstructor
public class DefaultRSQLVisitor implements RSQLVisitor<String, Object> {

    private final Table table;

    @Override
    public String visit(AndNode andNode, Object o) {
        return null;
    }

    @Override
    public String visit(OrNode orNode, Object o) {
        return null;
    }

    @Override
    public String visit(ComparisonNode comparisonNode, Object o) {
        return null;
    }
}
