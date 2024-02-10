package com.homihq.db2rest.rest.read.helper;

import com.homihq.db2rest.rsql.operators.SimpleRSQLOperators;
import com.homihq.db2rest.rsql.parser.MyBatisFilterVisitorParser;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class WhereBuilder{



    public void build(ReadContext context) {
        if(context.isUnion()) return;

        if(StringUtils.isNotBlank(context.filter)) {

            log.info("-Creating where condition -");

            Node rootNode = new RSQLParser(SimpleRSQLOperators.customOperators()).parse(context.filter);

            SqlCriterion condition = rootNode
                    .accept(new MyBatisFilterVisitorParser(context.from));

            context.addWhereClause(condition);

        }

    }
}
