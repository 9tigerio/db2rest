package com.homihq.db2rest.rsql;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.parser.JOOQRSQLVisitor;
import com.homihq.db2rest.schema.SchemaService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Query;
import org.springframework.stereotype.Service;
import schemacrawler.schema.Table;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilterBuilderService {

    private final SchemaService schemaService;

    public String getWhereClause(String tableName, String rSql) {

        Table table = schemaService.getTableByName(tableName).orElseThrow();

        Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rSql);
        return
                rootNode.accept(new DefaultRSQLVisitor(table));

    }

    public void getWhereClause(Query query, String tableName, String rSql) {
        Table table = schemaService.getTableByName(tableName).orElseThrow();

        Node rootNode = new RSQLParser().parse(rSql);
        Object o =
                rootNode.accept(new JOOQRSQLVisitor(table, query));

        log.info("o -> {}", o);
    }
}
