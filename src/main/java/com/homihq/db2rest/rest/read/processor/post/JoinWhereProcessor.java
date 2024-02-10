package com.homihq.db2rest.rest.read.processor.post;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.processor.pre.ReadPreProcessor;
import com.homihq.db2rest.rsql.operators.SimpleRSQLOperators;
import com.homihq.db2rest.rsql.parser.WhereFilterVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
//@Order(6)
public class JoinWhereProcessor implements ReadPreProcessor {
    @Override
    public void process(ReadContextV2 readContextV2) {
        if(StringUtils.isNotBlank(readContextV2.getFilter())) {

            log.info("**** Creating JOIN where condition *****");

            Node rootNode = new RSQLParser(SimpleRSQLOperators.customOperators()).parse(readContextV2.getFilter());

            SqlCriterion condition = rootNode
                    .accept(new WhereFilterVisitor(readContextV2.getRootTable()));

            readContextV2.addWhereCondition(condition);

        }
    }
}
