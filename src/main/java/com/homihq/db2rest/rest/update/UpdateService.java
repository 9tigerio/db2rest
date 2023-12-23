package com.homihq.db2rest.rest.update;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


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
        if(!db2RestConfigProperties.isValidSchema(schemaName)) {
            throw new RuntimeException("Invalid schema name");
        }

        Table<?> table =
                schemaService.getTableByNameAndSchema(schemaName, tableName)
                        .orElseThrow(() -> new RuntimeException("Table not found"));


        UpdateConditionStep<?> updateConditionStep = dslContext.update(table)
                .set(data) //TODO - fix data types
                .where(whereBuilder.create(table , tableName,  filter));

        String sql = updateConditionStep.getSQL();
        List<Object> bindValues = updateConditionStep.getBindValues();
        log.info("SQL - {}", sql); // TODO make it conditional
        log.info("Bind variables - {}", bindValues);

        jdbcTemplate.update(sql , bindValues.toArray());
    }

}
