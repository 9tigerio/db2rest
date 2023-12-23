package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.rest.query.helper.SelectBuilder;
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
    private final Db2RestConfigProperties db2RestConfigProperties;

    public Object findAllByJoinTable(String schemaName, String tableName, String select, String filter, String joinTable) {


        Query query = createQuery(schemaName, tableName,select,filter, joinTable);

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);


        return jdbcTemplate.queryForList(sql, bindValues.toArray());
    }

    @Transactional(readOnly = true)
    public Object findAll(String schemaName, String tableName, String select, String filter) {

        Query query = createQuery(schemaName, tableName,select,filter, null);

        String sql = query.getSQL();
        List<Object> bindValues = query.getBindValues();
        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        return jdbcTemplate.queryForList(sql, bindValues.toArray());

    }

    private Query createQuery(String schemaName, String tableName, String select, String filter, String joinTable) {

        if(!db2RestConfigProperties.isValidSchema(schemaName)) {
            throw new RuntimeException("Invalid schema name");
        }

        List<String> columns = StringUtils.isBlank(select) ?  List.of() : List.of(select.split(","));

        Table<?> table =
                schemaService.getTableByNameAndSchema(schemaName, tableName)
                        .orElseThrow(() -> new RuntimeException("Table not found"));
        SelectJoinStep<Record> selectJoinStep;
        if(columns.isEmpty()) {
            selectJoinStep = dslContext.select(asterisk()).from(table);
        }
        else{
            List<Field<?>> fields =  selectBuilder.build(table, columns);

            selectJoinStep = dslContext.select(fields).from(table);

        }

        if(StringUtils.isNotBlank(joinTable)) {
            Table<?> jTable =
                    schemaService.getTableByNameAndSchema(schemaName, joinTable)
                            .orElseThrow(() -> new RuntimeException("Table not found"));
            createJoin(table, jTable, selectJoinStep);
        }

        Condition whereCondition = whereBuilder.create(tableName, filter);

        return selectJoinStep.where(whereCondition);
    }

    private void createJoin(Table<?> table, Table<?> jTable, SelectJoinStep<Record> selectJoinStep) {
        selectJoinStep.innerJoin(jTable).on(createJoinCondition(table, jTable));
    }

    private Condition createJoinCondition(Table<?> table, Table<?> jTable) {
        return schemaService.createJoin2(table, jTable);
    }



}
