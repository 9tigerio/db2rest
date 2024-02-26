package com.homihq.db2rest.rest.read.processor;

import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.model.DbWhere;
import com.homihq.db2rest.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.rsql.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.JdbcSchemaManager;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(8)
@RequiredArgsConstructor
public class RootWhereProcessor implements ReadProcessor {

    private final JdbcSchemaManager jdbcSchemaManager;

    @Override
    public void process(ReadContext readContext) {
        if(StringUtils.isNotBlank(readContext.getFilter())) {
            readContext.createParamMap();

            DbWhere dbWhere = new DbWhere(
                    readContext.getTableName(),
                    readContext.getRoot(), readContext.getCols(), readContext.getParamMap());

            log.info("-Creating root where condition -");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(readContext.getFilter());

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, jdbcSchemaManager.getDialect()));

            log.info("Where - {}", where);

            log.info("param map - {}", readContext.getParamMap());

            readContext.setRootWhere(where);


        }
    }
}
