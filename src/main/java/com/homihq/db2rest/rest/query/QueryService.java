package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.rest.query.helper.SelectBuilder;
import com.homihq.db2rest.rest.query.model.JoinTable;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    private Query createQuery(String schemaName, String tableName, String select, String filter, String joinTable) {

        db2RestConfigProperties.verifySchema(schemaName);

        List<String> columns = StringUtils.isBlank(select) ?  List.of() : List.of(select.split(","));

        List<JoinTable> joinTableList =
                getJoinTableDetails(joinTable);

        log.info("joinTableList - {}" , joinTableList);

        Table<?> table = schemaService.getTableByNameAndSchema(schemaName, tableName);

        SelectJoinStep<Record> selectJoinStep;
        if(columns.isEmpty()) {
            selectJoinStep = dslContext.select(asterisk()).from(table);
        }
        else{
            List<Field<?>> fields =  selectBuilder.build(table, columns);
            selectJoinStep = dslContext.select(fields).from(table);
        }

        if(!joinTableList.isEmpty()) {

            for (JoinTable jt : joinTableList) {

                Table<?> jTable = schemaService.getTableByNameAndSchema(schemaName, jt.name());
                createJoin(table, jTable, selectJoinStep);
            }
        }

        Condition whereCondition = whereBuilder.create(tableName, filter);

        return selectJoinStep.where(whereCondition);
    }

    private List<JoinTable> getJoinTableDetails(String joinTable) {
        List<JoinTable> joinTableList = new ArrayList<>();

        if(StringUtils.isNotBlank(joinTable)) {
            //get fields of join table
            String [] joinTables = joinTable.split(";");

            if(joinTables.length > 1) throw new RuntimeException("Only 2 tables are allowed");


            for(String jt : joinTables) {
                log.info("jt - {}", jt);
                //joinTableList
                String tName = jt.substring(0, jt.indexOf("[") );
                String parts = jt.substring(jt.indexOf("[") + 1 , jt.indexOf("]"));

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

                    joinTableList.add(new JoinTable(tName, jtCols, filter));
                }
                else{
                    joinTableList.add(new JoinTable(tName, jtCols, filter));
                }


            }
        }

        return joinTableList;
    }

    private void createJoin(Table<?> table, Table<?> jTable, SelectJoinStep<Record> selectJoinStep) {
        selectJoinStep.innerJoin(jTable).on(createJoinCondition(table, jTable));
    }

    private Condition createJoinCondition(Table<?> table, Table<?> jTable) {
        return schemaService.createJoin(table, jTable);
    }



}
