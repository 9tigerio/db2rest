package com.homihq.db2rest.rest.read.processor.pre;


import com.homihq.db2rest.exception.InvalidOperatorException;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbJoin;
import com.homihq.db2rest.rest.read.model.DbTable;
import com.homihq.db2rest.rest.read.processor.rsql.operator.CustomRSQLOperators;
import com.homihq.db2rest.rest.read.processor.rsql.operator.handler.OperatorMap;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;


@Component
@Slf4j
@Order(6)
@RequiredArgsConstructor
public class JoinTableFieldPreProcessor implements ReadPreProcessor {

    private final SchemaManager schemaManager;
    private final OperatorMap operatorMap;
    @Override
    public void process(ReadContextV2 readContextV2) {
        List<JoinDetail> joins = readContextV2.getJoins();

        if(Objects.isNull(joins) || joins.isEmpty()) return;

        DbTable rootTable = readContextV2.getRoot();

        for(JoinDetail joinDetail : joins) {
            String tableName = joinDetail.table();

            DbTable table = schemaManager.getTableV2(tableName);

            List<DbColumn> columnList = addColumns(table, joinDetail.fields());

            readContextV2.addColumns(columnList);

            addJoin(table, rootTable, joinDetail, readContextV2);
        }
    }

    private void addJoin(DbTable table, DbTable rootTable, JoinDetail joinDetail, ReadContextV2 readContextV2) {
        DbJoin join = new DbJoin();
        join.setTableName(table.name());
        join.setAlias(table.alias());
        join.setJoinType(joinDetail.getJoinType());

        addCondition(table, rootTable, joinDetail, join);

        readContextV2.addJoin(join);

    }

    private void addCondition(DbTable table, DbTable rootTable, JoinDetail joinDetail, DbJoin dbJoin) {

        if(joinDetail.hasOn()) {
            int onIdx = 1;
            for(String on : joinDetail.on()) {
                log.info("Processing - on : {}", on);
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

        log.info("{} | {} | {}", operator, left, right);

        DbColumn leftColumn = rootTable.buildColumn(left);
        DbColumn rightColumn = table.buildColumn(right);

        if(onIdx == 1) {
            dbJoin.addOn(leftColumn, operator, rightColumn);
        }
        else{
            dbJoin.addAndCondition(leftColumn, operator, rightColumn);
        }

    }

    protected String getOperator(String on) {
        return
                CustomRSQLOperators.customOperators()
                        .stream()
                        .map(ComparisonOperator::getSymbol)
                        .filter(op -> StringUtils.containsIgnoreCase(on, op))
                        .findFirst().orElseThrow(() -> new InvalidOperatorException("Operator not supported", ""));


    }

    private DbColumn createColumn(String columnName, DbTable table) {
        Column column = table.lookupColumn(columnName);

        return new DbColumn(table.name(), columnName, getJdbcType(column) , column, "", table.alias());
    }

    private List<DbColumn> addColumns(DbTable table, List<String> fields) {

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the given table

        log.info("Fields - {}", fields);

        List<DbColumn> columnList = new ArrayList<>();

        if(Objects.isNull(fields)) {

            //include all fields of root table
            List<DbColumn> columns =
                    table.table()
                    .getColumns()
                            .stream()
                            .map(column -> createColumn(column.getName(), table))
                            .toList();

            columnList.addAll(columns);
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    fields.stream()
                            .map(col -> createColumn(col, table))
                            .toList();
            columnList.addAll(columns);
        }

        return columnList;
    }


}
