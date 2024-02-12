package com.homihq.db2rest.rest.read.processor.post;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rsql.v1.parser.JoinOnParser;
import com.homihq.db2rest.schema.SchemaManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Order(3)
@RequiredArgsConstructor
@Deprecated
public class JoinPostProcessor implements ReadPostProcessor {

    private final SchemaManager schemaManager;
    private final JoinOnParser joinOnParser;
    @Override
    public void process(QueryExpressionDSL<SelectModel> queryExpressionDSL, ReadContextV2 readContextV2) {
        log.info("Join processor");
        /*
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

         */
    }

    private void createInnerJoin(MyBatisTable rootTable, MyBatisTable childTable, JoinDetail joinDetail, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
        log.info("Processing inner join");

        joinOnParser.parse(rootTable, childTable, joinDetail, queryExpressionDSL);



    }

    private void createFullJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail joinDetail, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }

    private void createLeftJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail joinDetail, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }

    private void createRightJoin(MyBatisTable rootTable, MyBatisTable myBatisTable, JoinDetail joinDetail, QueryExpressionDSL<SelectModel> queryExpressionDSL) {
    }


}
