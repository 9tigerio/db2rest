package com.homihq.db2rest.rest.read.processor;

import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbJoin;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.model.DbWhere;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.rsql.operator.handler.OperatorMap;
import com.homihq.db2rest.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.rsql.visitor.BaseRSQLVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@Component
@Slf4j
@Order(6)
@RequiredArgsConstructor
public class JoinProcessor implements ReadProcessor {

    private final SchemaManager schemaManager;
    private final OperatorMap operatorMap;
    @Override
    public void process(ReadContext readContext) {
        List<JoinDetail> joins = readContext.getJoins();

        if(Objects.isNull(joins) || joins.isEmpty()) return;

        DbTable rootTable = readContext.getRoot();

        for(JoinDetail joinDetail : joins) {
            String tableName = joinDetail.table();

            DbTable table = schemaManager.getTable(tableName);

            List<DbColumn> columnList = addColumns(table, joinDetail.fields());

            readContext.addColumns(columnList);

            addJoin(table, rootTable, joinDetail, readContext);
        }
    }

    private void addJoin(DbTable table, DbTable rootTable, JoinDetail joinDetail, ReadContext readContext) {
        DbJoin join = new DbJoin();
        join.setTableName(table.name());
        join.setAlias(table.alias());
        join.setJoinType(joinDetail.getJoinType());

        addCondition(table, rootTable, joinDetail, join);

        processFilter(table, joinDetail, join, readContext);

        readContext.addJoin(join);

    }

    private void processFilter(DbTable table, JoinDetail joinDetail, DbJoin join,
                               ReadContext readContext) {
        if(joinDetail.hasFilter()){
            readContext.createParamMap();

            DbWhere dbWhere = new DbWhere(
                    table.name(),
                    table,table.buildColumns(), readContext.getParamMap());


            Node rootNode = RSQLParserBuilder.newRSQLParser().parse(joinDetail.filter());

            String where = rootNode
                    .accept(new BaseRSQLVisitor(
                            dbWhere, schemaManager.getDialect()));

            join.addAdditionalWhere(where);


        }

    }

    private void addCondition(DbTable table, DbTable rootTable, JoinDetail joinDetail, DbJoin dbJoin) {

        if(joinDetail.hasOn()) {
            int onIdx = 1;
            for(String on : joinDetail.on()) {
                processOn(on, onIdx, table, rootTable, dbJoin);
                onIdx++;
            }
        }

    }

    private void processOn(String onExpression, int onIdx, DbTable table, DbTable rootTable, DbJoin dbJoin) {
        String rSqlOperator = this.operatorMap.getRSQLOperator(onExpression);
        String operator = this.operatorMap.getSQLOperator(rSqlOperator);

        String left = onExpression.substring(0, onExpression.indexOf(rSqlOperator)).trim();
        String right = onExpression.substring(onExpression.indexOf(rSqlOperator) + rSqlOperator.length()).trim();


        DbColumn leftColumn = rootTable.buildColumn(left);
        DbColumn rightColumn = table.buildColumn(right);

        if(onIdx == 1) {
            dbJoin.addOn(leftColumn, operator, rightColumn);
        }
        else{
            dbJoin.addAndCondition(leftColumn, operator, rightColumn);
        }

    }

    private List<DbColumn> addColumns(DbTable table, List<String> fields) {

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the given table

        log.info("Fields - {}", fields);

        List<DbColumn> columnList = new ArrayList<>();

        if(Objects.isNull(fields)) {//include all fields of root table
            columnList.addAll( table.buildColumns());
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    fields.stream()
                            .map(table::buildColumn)
                            .toList();
            columnList.addAll(columns);
        }

        return columnList;
    }


}
