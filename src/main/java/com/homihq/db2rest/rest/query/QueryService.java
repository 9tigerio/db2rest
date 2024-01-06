package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.JoinBuilder;
import com.homihq.db2rest.rest.query.helper.QueryContext;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.rest.query.helper.SelectBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jooq.Record;
import org.mybatis.dynamic.sql.SqlTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static org.jooq.impl.DSL.*;
import org.jooq.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {

    private final JdbcTemplate jdbcTemplate;
    private final SelectBuilder selectBuilder;
    private final JoinBuilder joinBuilder;
    private final WhereBuilder whereBuilder;

    private final Db2RestConfigProperties db2RestConfigProperties;

    public Object findAll(String schemaName, String tableName, String select, String filter,
                                     Pageable pageable) {
        QueryContext ctx = QueryContext.builder().from(SqlTable.of(tableName))
            .schemaName(schemaName).tableName(tableName).select(select).filter(filter).build();


        selectBuilder.build(ctx);
        joinBuilder.build(ctx);
        whereBuilder.build(ctx);

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


    private void addOrderBy(SelectJoinStep<Record> selectJoinStep, Pageable pageable) {
        log.info("pageable - {}", pageable);

        log.info("pageable.getSort().isSorted() - {}", pageable.getSort().isSorted());

        if(pageable.getSort().isSorted()) {
            Sort sort = pageable.getSort();
            log.info("sort - {}", sort);

            sort.forEach(i -> selectJoinStep
                    .orderBy(field(i.getProperty()).sort(
                            i.getDirection().isAscending() ? SortOrder.ASC : SortOrder.DESC))
                    );

        }
    }



}
