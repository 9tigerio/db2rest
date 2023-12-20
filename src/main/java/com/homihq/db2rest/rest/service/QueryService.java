package com.homihq.db2rest.rest.service;


import com.homihq.db2rest.rest.handler.SelectColumnBuilder;
import com.homihq.db2rest.rest.handler.SelectColumns;
import com.homihq.db2rest.rsql.FilterBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static org.jooq.impl.DSL.*;
import org.jooq.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = "WHERE";


    private final JdbcTemplate jdbcTemplate;
    private final FilterBuilderService filterBuilderService;
    private final SelectColumnBuilder selectColumnBuilder;

    private final DSLContext dslContext;

    @Transactional(readOnly = true)
    public Object findJooq(String tableName, String select, String rSql) { // No join
        List<String> columns = StringUtils.isBlank(select) ?  List.of() : List.of(select.split(","));

        Query query;

        if(columns.isEmpty()) {
            query = dslContext.select(field(  ".*" )).from(tableName);
            log.info("JOOQ SQL - {}", query.getSQL());
        }
        else{
            SelectColumns selectColumns = selectColumnBuilder.build(tableName, tableName, columns, true);

            List<Field<Object>> fields = selectColumns.selectColumnList()
                    .stream().map(sc -> field(  sc.getCol()))
                    .toList();
            SelectJoinStep<Record> selectFromStep = dslContext.select(fields).from(tableName);

            try {
                this.filterBuilderService.getWhereClause(selectFromStep, tableName, rSql);
            }
            catch(Exception e) {
                e.printStackTrace();
            }

        }





        return null;

    }

    @Transactional(readOnly = true)
    public Object find(String tableName, String select, String rSql) {

        List<String> columns = StringUtils.isBlank(select) ?  List.of() : List.of(select.split(","));

        SelectColumns selectColumns = selectColumnBuilder.build(tableName, tableName, columns, true);

        String sql = SELECT + selectColumns.getSelect() + FROM + selectColumns.getTables(tableName) ;


        if(StringUtils.isNotBlank(rSql)) {
            sql = sql + " " + WHERE + " " + filterBuilderService.getWhereClause(tableName , rSql);
        }

        log.info("sql - {}", sql);

        return jdbcTemplate.queryForList(sql);

    }


    public Object findByJoinTable(String tableName, String keys, String joinTable, String select, String rSql) {
        return null;
    }
}
