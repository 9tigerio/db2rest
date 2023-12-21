package com.homihq.db2rest.rest.filter;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.parser.DefaultRSQLVisitor;
import com.homihq.db2rest.schema.SchemaService;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.noCondition;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilterBuilder {

    private final SchemaService schemaService;

    public Condition create(String tableName, String rSql) {


        if(StringUtils.isNotBlank(rSql)) {

            Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(rSql);

            log.info("root node - {}", rootNode);

            return rootNode.accept(new DefaultRSQLVisitor(schemaService , tableName));

        }

        return noCondition();
    }
}
