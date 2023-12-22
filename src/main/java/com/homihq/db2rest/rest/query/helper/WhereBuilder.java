package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.parser.DefaultRSQLVisitor;
import com.homihq.db2rest.schema.SchemaService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.noCondition;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhereBuilder {

    private final SchemaService schemaService;

    public Condition create(String tableName, String rSql) {

        if(StringUtils.isNotBlank(rSql)) {
            Table<?> table =
            schemaService.getTableByName(tableName).orElseThrow(() -> new RuntimeException("Table not found"));

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rSql);

            return rootNode.accept(new DefaultRSQLVisitor(schemaService , tableName, (Table<Record>) table));

        }

        return noCondition();
    }
}
