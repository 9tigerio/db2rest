package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.DeleteOpNotAllowedException;
import com.homihq.db2rest.rest.query.helper.WhereBuilder;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.jooq.impl.DSL.*;
import org.jooq.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final JdbcTemplate jdbcTemplate;
    private final WhereBuilder whereBuilder;
    private final DSLContext dslContext;
    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaService schemaService;

    @Transactional
    public void delete(String schemaName, String tableName, String filter) {

        if(StringUtils.isBlank(filter) && db2RestConfigProperties.isAllowSafeDelete()) {
            throw new DeleteOpNotAllowedException(false);
        }
        else{

            db2RestConfigProperties.verifySchema(schemaName);

            Table<?> table =
                    schemaService.getTableByNameAndSchema(schemaName, tableName)
                            .orElseThrow(() -> new RuntimeException("Table not found"));


            DeleteUsingStep<?> deleteUsingStep =
                    dslContext.deleteFrom(table);

            Condition whereCondition =
                    whereBuilder.create(table, tableName, filter);

            DeleteConditionStep<?> deleteConditionStep = deleteUsingStep.where(whereCondition);

            String sql = deleteConditionStep.getSQL();
            List<Object> bindValues = deleteConditionStep.getBindValues();

            log.info("SQL - {}", sql);
            log.info("Bind variables - {}", bindValues);

            jdbcTemplate.update(sql , bindValues.toArray());
        }

    }
}
