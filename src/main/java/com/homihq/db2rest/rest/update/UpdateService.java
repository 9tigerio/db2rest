package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.jooq.impl.DSL.field;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {

    private final JdbcTemplate jdbcTemplate;
    private final WhereBuilder whereBuilder;
    private final DSLContext dslContext;
    private final SchemaService schemaService;
    private final Db2RestConfigProperties db2RestConfigProperties;
    @Transactional
    public void update(String schemaName, String tableName, Map<String,Object> data, String filter) {
        db2RestConfigProperties.verifySchema(schemaName);

        /*
        Table<?> table = schemaService.getTableByNameAndSchema(schemaName, tableName);

        UpdateSetFirstStep<?> updateSetFirstStep = dslContext.update(table);

        UpdateSetMoreStep<?> updateSetMoreStep = null;

        for(String key : data.keySet()) {
            updateSetMoreStep = updateSetFirstStep.set(field(key) , data.get(key));
        }

        UpdateConditionStep<?> updateConditionStep;
        String sql;
        List<Object> bindValues;
        if(StringUtils.isNotBlank(filter)) {
            updateConditionStep = updateSetMoreStep.where(whereBuilder.create(table , tableName,  filter));
            sql = updateConditionStep.getSQL();
            bindValues = updateConditionStep.getBindValues();
        }
        else{
            sql = updateSetMoreStep.getSQL();
            bindValues = updateSetMoreStep.getBindValues();
        }


        log.info("SQL - {}", sql); // TODO make it conditional
        log.info("Bind variables - {}", bindValues);

        jdbcTemplate.update(sql , bindValues.toArray());

         */
    }

}
