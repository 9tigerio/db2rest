package com.homihq.db2rest.rest.service;

import com.homihq.db2rest.rest.filter.FilterBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.UpdateConditionStep;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import org.jooq.Record;
import static org.jooq.impl.DSL.table;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatchService {

    private final JdbcTemplate jdbcTemplate;
    private final FilterBuilder filterBuilder;
    private final DSLContext dslContext;

    @Transactional
    public int patch(String tableName, Map<String,Object> data, String rSql) {
        UpdateConditionStep<Record> updateConditionStep = dslContext.update(table(tableName))
                .set(data) //TODO - fix data types
                .where(filterBuilder.create(tableName , rSql));

        String sql = updateConditionStep.getSQL();
        List<Object> bindValues = updateConditionStep.getBindValues();
        log.info("SQL - {}", sql); // TODO make it conditional
        log.info("Bind variables - {}", bindValues);

        return jdbcTemplate.update(sql , bindValues);
    }

}
