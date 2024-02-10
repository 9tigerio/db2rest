package com.homihq.db2rest.rest.read.v2.processor.post;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.v2.dto.JoinDetail;
import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import com.homihq.db2rest.rsql.operators.SimpleRSQLOperators;
import com.homihq.db2rest.rsql.parser.JoinOnParser;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.join.JoinCriterion;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Order(3)
@RequiredArgsConstructor
public class JoinProcessor implements ReadPostProcessor {

    private final SchemaManager schemaManager;
    private final JoinOnParser joinOnParser;
    @Override
    public void process(QueryExpressionDSL<SelectModel> queryExpressionDSL, ReadContextV2 readContextV2) {
        log.info("Join processor");
        if(Objects.nonNull(readContextV2.getJoins()) && !readContextV2.getJoins().isEmpty()) {

            MyBatisTable rootTable = readContextV2.getRootTable();

            for(JoinDetail join : readContextV2.getJoins()) {
                //log.info("## Join -> {}", join);
                MyBatisTable childTable =
                schemaManager.getTable(join.table());

                switch (join.getJoinType()) {

                    case INNER -> createInnerJoin(rootTable, childTable, join, queryExpressionDSL);
                    case RIGHT -> createRightJoin(rootTable, childTable, join, queryExpressionDSL);
                    case LEFT -> createLeftJoin(rootTable, childTable, join, queryExpressionDSL);
                    case FULL ->  createFullJoin(rootTable, childTable, join, queryExpressionDSL);

                    default -> throw new RuntimeException("Unknown JOIN type - " + join.getJoinType());

                }

                rootTable = childTable;

            }
        }
    }

    private void createInnerJoin(MyBatisTable rootTable, MyBatisTable childTable, JoinDetail join, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
        log.info("Processing inner join");

        joinOnParser.parse(rootTable, childTable, join.on(), queryExpressionDSL);

    }

    private void createFullJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail join, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }

    private void createLeftJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail join, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }

    private void createRightJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail join, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }


}
