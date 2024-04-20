package com.homihq.db2rest.jdbc.processor;


import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.core.model.DbWhere;
import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(8)
@RequiredArgsConstructor
public class RootWhereProcessor implements ReadProcessor {
    private final JdbcSchemaCache jdbcSchemaCache;
    @Override
    public void process(ReadContext readContext) {
        if(StringUtils.isNotBlank(readContext.getFilter())) {
            readContext.createParamMap();

            DbWhere dbWhere = new DbWhere(
                    readContext.getTableName(),
                    readContext.getRoot(), readContext.getCols(), readContext.getParamMap(), "read");

            log.debug("-Creating root where condition -");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(readContext.getFilter());

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, jdbcSchemaCache.getDialect()));

            log.debug("Where - {}", where);
            log.debug("param map - {}", readContext.getParamMap());

            readContext.setRootWhere(where);


        }
    }
}
