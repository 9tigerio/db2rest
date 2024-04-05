package com.homihq.db2rest.jdbc.processor;

import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbJoin;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.core.model.DbWhere;
import com.homihq.db2rest.jdbc.rest.read.dto.JoinDetail;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import static com.homihq.db2rest.jdbc.rsql.operator.handler.OperatorMap.*;
import com.homihq.db2rest.jdbc.rsql.parser.RSQLParserBuilder;
import com.homihq.db2rest.jdbc.rsql.visitor.BaseRSQLVisitor;
import static com.homihq.db2rest.schema.AliasGenerator.*;
import com.homihq.db2rest.schema.SchemaCache;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Order(6)
@RequiredArgsConstructor
public class JoinProcessor implements ReadProcessor {

    private final SchemaCache schemaCache;
    private final Dialect dialect;

    @Override
    public void process(ReadContext readContext) {
        List<JoinDetail> joins = readContext.getJoins();

        if(Objects.isNull(joins) || joins.isEmpty()) return;

        DbTable rootTable = readContext.getRoot();

        List<DbTable> allJoinTables = new ArrayList<>();
        allJoinTables.add(rootTable);

        for(JoinDetail joinDetail : joins) {

            rootTable = reviewRootTable(allJoinTables, joinDetail, rootTable);

            String tableName = joinDetail.table();
            DbTable table = schemaCache.getTable(tableName);
            table = table.copyWithAlias(getAlias(tableName));
            List<DbColumn> columnList = addColumns(table, joinDetail.fields());
            readContext.addColumns(columnList);
            addJoin(table, rootTable, joinDetail, readContext);


            allJoinTables.add(rootTable);

            rootTable = table;
        }
    }

    private DbTable reviewRootTable(List<DbTable> allJoinTables, JoinDetail joinDetail, DbTable rootTable) {
        if(allJoinTables.size() == 1) return rootTable;

        if(joinDetail.hasWith()) {
            //check if existing table
            String withTable = joinDetail.withTable();
            Optional<DbTable> newRoot = allJoinTables.stream()
                    .filter(t -> StringUtils.equalsIgnoreCase(withTable, t.name()))
                    .findFirst();

            //look in cache
            return newRoot.orElseGet(() -> schemaCache.getTable(withTable));
        }

        return rootTable;
    }

    private void addJoin(DbTable table, DbTable rootTable, JoinDetail joinDetail, ReadContext readContext) {
        DbJoin join = new DbJoin();
        join.setTableName(table.fullName());
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
                            dbWhere, dialect));

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
        String rSqlOperator = getRSQLOperator(onExpression);
        String operator = getSQLOperator(rSqlOperator);

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

        log.debug("Fields - {}", fields);

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
