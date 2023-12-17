package com.homihq.db2rest.rest.service;

import com.homihq.db2rest.rest.handler.SelectColumnBuilder;
import com.homihq.db2rest.rest.handler.SelectColumns;
import com.homihq.db2rest.rsql.FilterBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    @Transactional(readOnly = true)
    public Object find(String tableName, String select, String embed, String rSql) {

        List<String> columns = List.of();

        if(StringUtils.isNotBlank(select)) {
            columns = List.of(select.split(","));
        }

        SelectColumns selectColumns = selectColumnBuilder.build(tableName, tableName, columns, true);

        SelectColumns embedColumns = null;

        if(StringUtils.isNotBlank(embed)) {
            List<String> embeddedTables = List.of(embed.split(";"));
            embedColumns = selectColumnBuilder.buildEmbeded(embeddedTables);
        }


        String sql = SELECT + selectColumns.getSelect();

        sql = Objects.nonNull(embedColumns) ?  sql + " , " + embedColumns.getSelect() : sql;

        sql = sql + FROM + selectColumns.getTables(tableName) ;

        sql = Objects.nonNull(embedColumns) ?  sql + " , " + embedColumns.getTables(null) : sql;


        if(StringUtils.isNotBlank(rSql)) {
            sql = sql + " " + WHERE + " " + filterBuilderService.getWhereClause(tableName , rSql);
        }

        log.info("sql - {}", sql);

        return jdbcTemplate.queryForList(sql);

    }


}
