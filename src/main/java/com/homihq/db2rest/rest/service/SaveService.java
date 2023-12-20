package com.homihq.db2rest.rest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.jooq.Record;
import static org.jooq.impl.DSL.table;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveService {

    private final JdbcTemplate jdbcTemplate;
    private final DSLContext dslContext;

    @Transactional
    public void save(String tableName, Map<String,Object> data) {
        InsertValuesStepN<Record> insertValuesStepN =
        dslContext.insertInto(table(tableName)).columns(getColumns(data))
                .values(getValues(data));

        String sql = insertValuesStepN.getSQL();
        List<Object> bindValues = insertValuesStepN.getBindValues();
        log.info("SQL - {}", sql); // TODO make it conditional
        log.info("Bind variables - {}", bindValues);

        jdbcTemplate.update(sql , bindValues);
    }


    private Object[] getValues(Map<String, Object> data) {
        return  data.values().toArray();
    }

    private List<Field<Object>> getColumns(Map<String, Object> data) {
        return
        data.keySet().stream().map(DSL::field).toList();

    }

    @Transactional
    public void saveBulk(String tableName, boolean batch, List<Map<String, Object>> data) {
        //TODO - Apply batch param
        for(Map<String,Object> d : data) {
            save(tableName, d);
        }

    }
}
