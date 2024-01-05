package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.parser.DefaultRSQLVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import com.homihq.db2rest.schema.SchemaService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.noCondition;

@Component
@RequiredArgsConstructor
@Slf4j
public class WhereBuilder implements SqlQueryPartBuilder{

    private final SchemaService schemaService;
    private final SchemaManager schemaManager;

    @Deprecated
    public Condition create(String tableName, String rSql) {

        if(StringUtils.isNotBlank(rSql)) {
            Table<?> table =
            schemaService.getTableByName(tableName).orElseThrow(() -> new RuntimeException("Table not found"));

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rSql);

            return rootNode.accept(new DefaultRSQLVisitor(schemaService , tableName, table));

        }

        return noCondition();
    }

    @Deprecated
    public Condition create(Table<?> table, String tableName, String filter) {

        if(StringUtils.isNotBlank(filter)) {

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(filter);

            return rootNode.accept(new DefaultRSQLVisitor(schemaService , tableName, table));

        }

        return noCondition();
    }

    @Override
    public void build(QueryContext context) {
        if(StringUtils.isNotBlank(context.filter)) {

            log.info("-Creating where condition -");

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(context.filter);

            //RCondition condition = rootNode.accept(new BaseRSQLVisitor(schemaManager , context));

            //log.info("condition - {}", condition);

        }

    }
}
