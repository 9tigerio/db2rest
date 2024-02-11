package com.homihq.db2rest.rest.read.processor.pre;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;


import com.homihq.db2rest.rest.read.model.DbWhere;
import com.homihq.db2rest.rest.read.processor.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.rest.read.processor.rsql.visitor.BaseRSQLVisitor;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Order(6)
public class RootWhereProcessor implements ReadPreProcessor {
    @Override
    public void process(ReadContextV2 readContextV2) {
        if(StringUtils.isNotBlank(readContextV2.getFilter())) {

            Map<String,Object> paramMap = new HashMap<>();

            DbWhere dbWhere = new DbWhere(
                    readContextV2.getTableName(),
                    readContextV2.getRoot(),readContextV2.getCols(),paramMap);

            log.info("-Creating root where condition -");

            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(readContextV2.getFilter());

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere));

            log.info("Where - {}", where);

            log.info("param map - {}", paramMap);

            //readContextV2.addWhereCondition(condition);

        }
    }
}
