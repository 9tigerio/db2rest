package com.homihq.db2rest.rsql;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.operators.handler.jooq.JooqCustomRSQLOperators;
import com.homihq.db2rest.rsql.parser.JooqRSQLVisitor;
import com.homihq.db2rest.schema.SchemaService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.Record;
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

    public void getWhereClause(SelectJoinStep<Record> selectFromStep, String tableName, String rSql) {
        Table table = schemaService.getTableByName(tableName).orElseThrow();

        Node rootNode = new RSQLParser(JooqCustomRSQLOperators.customOperators()).parse(rSql);

        log.info("root node - {}", rootNode);


        Condition condition =
                rootNode.accept(new JooqRSQLVisitor(table));

        log.info("condition -> {}", condition);


        selectFromStep.where(condition);



    }
}
