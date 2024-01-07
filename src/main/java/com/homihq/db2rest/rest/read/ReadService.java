package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.read.helper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class ReadService {

    private final JdbcTemplate jdbcTemplate;
    private final SelectBuilder selectBuilder;
    private final JoinBuilder joinBuilder;
    private final WhereBuilder whereBuilder;
    private final LimitPaginationBuilder limitPaginationBuilder;
    private final SortBuilder sortBuilder;

    private final Db2RestConfigProperties db2RestConfigProperties;

    public Object findAll(String schemaName, String tableName, String select, String filter,
                                     Pageable pageable, Sort sort) {
        ReadContext ctx = ReadContext.builder().from(SqlTable.of(tableName))
                .pageable(pageable).sort(sort)
            .schemaName(schemaName).tableName(tableName).select(select).filter(filter).build();


        selectBuilder.build(ctx);
        joinBuilder.build(ctx);
        whereBuilder.build(ctx);
        limitPaginationBuilder.build(ctx);
        sortBuilder.build(ctx);

        String sql = ctx.prepareSQL();

        log.info("SQL - {}", sql);

        /*
        Query query = createQuery(schemaName, tableName,select,filter, joinTable, pageable);

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        return jdbcTemplate.queryForList(sql, bindValues.toArray());

         */

        return null;
    }





}
