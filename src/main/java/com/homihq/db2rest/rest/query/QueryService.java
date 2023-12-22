package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.rest.query.helper.SelectBuilder;
import com.homihq.db2rest.rest.query.helper.model.SelectColumns;
import com.homihq.db2rest.schema.SchemaService;
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

    private final JdbcTemplate jdbcTemplate;
    private final SelectBuilder selectBuilder;
    private final WhereBuilder whereBuilder;
    private final DSLContext dslContext;
    private final SchemaService schemaService;

    @Transactional(readOnly = true)
    public Object findAll(String tableName, String select, String filter) {

        Query query = createQuery(tableName,select,filter, null);

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();
        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        return jdbcTemplate.queryForList(sql, bindValues);

    }

    private Query createQuery(String tableName, String select, String filter, String joinTable) {
        List<String> columns = StringUtils.isBlank(select) ?  List.of() : List.of(select.split(","));

        if(columns.isEmpty()) {

            SelectJoinStep<Record> selectJoinStep =
             dslContext.select(asterisk())
                    .from(tableName);

            if(StringUtils.isNotBlank(joinTable)) {
                createJoin(tableName, joinTable, selectJoinStep);
                createJoinCondition(tableName, joinTable);

            }

            Condition whereCondition =
                    whereBuilder.create(tableName, filter);


            return selectJoinStep
                    .where(whereCondition);

        }
        else{
            SelectColumns selectColumns = selectBuilder.build(tableName, tableName, columns, true);
            List<Field<Object>> fields = selectColumns.selectColumnList()
                    .stream().map(sc -> field(  sc.getCol()))
                    .toList();

            SelectJoinStep<Record> selectJoinStep =
                    dslContext.select(fields).from(tableName);

            if(StringUtils.isNotBlank(joinTable)) {
                createJoin(tableName, joinTable, selectJoinStep);

            }
            Condition whereCondition = whereBuilder.create(tableName, filter);
            return selectJoinStep
                    .where(whereCondition);

        }
    }

    private void createJoin(String tableName, String joinTable, SelectJoinStep<Record> selectJoinStep) {
        selectJoinStep.innerJoin(joinTable).on(createJoinCondition(tableName, joinTable));
    }

    private Condition createJoinCondition(String tableName, String joinTable) {
        return schemaService.createJoin(tableName, joinTable);
    }


    public Object findAllByJoinTable(String tableName, String select, String filter, String joinTable) {

        Query query = createQuery(tableName,select,filter, joinTable);

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);


        return jdbcTemplate.queryForList(sql, bindValues.toArray());
    }
}
