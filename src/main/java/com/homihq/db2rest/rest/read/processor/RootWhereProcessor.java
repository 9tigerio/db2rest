package com.homihq.db2rest.rest.read.processor;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.model.DbWhere;
import com.homihq.db2rest.rest.read.processor.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.rest.read.processor.rsql.visitor.BaseRSQLVisitor;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(8)
public class RootWhereProcessor implements ReadProcessor {
    @Override
    public void process(ReadContextV2 readContextV2) {
        if(StringUtils.isNotBlank(readContextV2.getFilter())) {
            readContextV2.createParamMap();

            DbWhere dbWhere = new DbWhere(
                    readContextV2.getTableName(),
                    readContextV2.getRoot(),readContextV2.getCols(),readContextV2.getParamMap());

            log.info("-Creating root where condition -");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(readContextV2.getFilter());

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere));

            log.info("Where - {}", where);

            log.info("param map - {}", readContextV2.getParamMap());

            readContextV2.setRootWhere(where);


        }
    }
}
