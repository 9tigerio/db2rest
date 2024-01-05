package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.JoinBuilder;
import com.homihq.db2rest.rest.query.helper.QueryContext;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.rest.query.helper.SelectBuilder;
import com.homihq.db2rest.rest.query.model.JoinTable;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private final DSLContext dslContext;
    private final SchemaService schemaService;
    private final Db2RestConfigProperties db2RestConfigProperties;

    public Object findAllByJoinTable(String schemaName, String tableName, String select, String filter,
                                    @Deprecated String joinTable, Pageable pageable) {
        QueryContext ctx = QueryContext.builder()
            .schemaName(schemaName).tableName(tableName).select(select).filter(filter).build();


        selectBuilder.build(ctx);
        joinBuilder.build(ctx);
        whereBuilder.build(ctx);

        ctx.buildSQL();

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

    private JoinTable getJoinTableDetails(String joinTable) {

        if(StringUtils.isNotBlank(joinTable)) {
            //get fields of join table

            //joinTableList
            String tName = joinTable.substring(0, joinTable.indexOf("[") );
            String parts = joinTable.substring(joinTable.indexOf("[") + 1 , joinTable.indexOf("]"));

            log.info("tName - {}", tName);
            log.info("parts - {}", parts);

            String select;
            String filter = null;
            List<String> jtCols = null;

            if(StringUtils.isNotBlank(parts)) {
                //get select & filter
                String [] innerParts = parts.split("\\|");

                if(innerParts.length == 2) {
                    select = innerParts[0];
                    filter = innerParts[1];

                }
                else{
                    select = innerParts[0];

                }

                log.info("select - {}", select);
                log.info("filter - {}", filter);

                if(StringUtils.isNotBlank(select)) {
                    select = select.replace("select=", "");
                    jtCols = List.of(select.split(","));
                }

                if(StringUtils.isNotBlank(filter)) {
                    filter = filter.replace("filter=", "");

                }

                return new JoinTable(tName, jtCols, filter);
            }

            return new JoinTable(tName, jtCols, filter);

        }

        return null;

    }





}
