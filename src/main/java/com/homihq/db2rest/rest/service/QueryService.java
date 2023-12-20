package com.homihq.db2rest.rest.service;

import com.homihq.db2rest.rest.filter.FilterBuilder;
import com.homihq.db2rest.rest.handler.SelectColumnBuilder;
import com.homihq.db2rest.rest.handler.SelectColumns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.jooq.impl.DSL.*;
import org.jooq.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {

    private final JdbcTemplate jdbcTemplate;
    private final SelectColumnBuilder selectColumnBuilder;
    private final FilterBuilder filterBuilder;
    private final DSLContext dslContext;


    @Transactional(readOnly = true)
    public List<Object> findAll(String tableName, String cols, String filter) {

        // No join
        List<String> columns = StringUtils.isBlank(cols) ?  List.of() : List.of(cols.split(","));
        Query query;
        if(columns.isEmpty()) {

            Condition whereCondition =
                    filterBuilder.create(tableName, filter);
            query = dslContext.selectFrom(tableName).where(whereCondition);

        }
        else{
            SelectColumns selectColumns = selectColumnBuilder.build(tableName, tableName, columns, true);

            List<Field<Object>> fields = selectColumns.selectColumnList()
                    .stream().map(sc -> field(  sc.getCol()))
                    .toList();

            Condition whereCondition = filterBuilder.create(tableName, filter);

            query = dslContext.select(fields).from(tableName).where(whereCondition);

        }

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();
        log.info("SQL - {}", sql); // TODO make it conditional
        log.info("Bind variables - {}", bindValues);

        return Collections.singletonList(jdbcTemplate.queryForList(sql, bindValues));

    }


    public Object findByJoinTable(String tableName, String keys, String joinTable, String select, String rSql) {
        return null;
    }
}
