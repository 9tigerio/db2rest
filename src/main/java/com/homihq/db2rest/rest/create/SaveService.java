package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.schema.SchemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.GeneralInsertDSL;
import org.mybatis.dynamic.sql.insert.render.GeneralInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mybatis.dynamic.sql.insert.GeneralInsertDSL.insertInto;



@Service
@Slf4j
@RequiredArgsConstructor
public class SaveService {

    private final JdbcTemplate jdbcTemplate;
    private final DSLContext dslContext;
    private final Db2RestConfigProperties db2RestConfigProperties;
    private final SchemaService schemaService;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public void save(String schemaName, String tableName, Map<String,Object> data) {
        GeneralInsertStatementProvider insertStatement = createInsertSQL2(schemaName, tableName, data);

        log.info("SQL - {}", insertStatement.getInsertStatement());
        log.info("SQL - row - {}", insertStatement.getParameters());

        int rows = namedParameterJdbcTemplate.update(insertStatement.getInsertStatement(), insertStatement.getParameters());

        log.info("Inserted - {} row(s)", rows);

    }

    private GeneralInsertStatementProvider createInsertSQL2(String schemaName, String tableName, Map<String, Object> data) {
        db2RestConfigProperties.verifySchema(schemaName);

        SqlTable table = SqlTable.of(tableName);
        GeneralInsertDSL generalInsertDSL = insertInto(table);

        for(String key : data.keySet()) {
            generalInsertDSL.set(table.column(key)).toValue(data.get(key));
        }

        return generalInsertDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER);

    }

    @Transactional
    public void saveBulk(String schemaName, String tableName, List<Map<String, Object>> dataList) {

        if(Objects.isNull(dataList) || dataList.isEmpty()) throw new RuntimeException("No data provided");

        InsertValuesStepN<?> insertValuesStepN = createInsertSQL(schemaName, tableName, dataList.get(0));

        String sql = insertValuesStepN.getSQL();
        List<Object> bindValues = insertValuesStepN.getBindValues();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        List<Object[]> batchData = new ArrayList<>();

        for(Map<String, Object> d : dataList) {
            batchData.add(d.values().toArray());
        }

        jdbcTemplate.batchUpdate(sql, batchData);

    }

    private InsertValuesStepN<?> createInsertSQL(String schemaName, String tableName, Map<String, Object> data) {
        db2RestConfigProperties.verifySchema(schemaName);

        Table<?> table = schemaService.getTableByNameAndSchema(schemaName, tableName);

        return dslContext.insertInto(table).columns(getColumns(data))
                .values(getValues(data));
    }


    private Object[] getValues(Map<String, Object> data) {
        return  data.values().toArray();
    }

    private List<Field<Object>> getColumns(Map<String, Object> data) {
        return
        data.keySet().stream().map(DSL::field).toList();

    }


}
