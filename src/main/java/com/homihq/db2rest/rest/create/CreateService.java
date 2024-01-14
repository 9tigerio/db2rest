package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.mybatis.DB2RestRenderingStrategy;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.insert.BatchInsertDSL;
import org.mybatis.dynamic.sql.insert.GeneralInsertDSL;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.GeneralInsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import static org.mybatis.dynamic.sql.insert.BatchInsertDSL.insert;
import static org.mybatis.dynamic.sql.insert.GeneralInsertDSL.insertInto;


@Service
@Slf4j
@RequiredArgsConstructor
public class CreateService {

    private final Db2RestConfigProperties db2RestConfigProperties;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DB2RestRenderingStrategy db2RestRenderingStrategy = new DB2RestRenderingStrategy();

    @Transactional
    public int save(String schemaName, String tableName, Map<String,Object> data, String tsid, String tsidType) {
        //db2RestConfigProperties.verifySchema(schemaName);

        processTSID(data, tsid, tsidType);

        SqlTable table = SqlTable.of(tableName);
        GeneralInsertDSL generalInsertDSL = insertInto(table);

        for(String key : data.keySet()) {
            generalInsertDSL.set(table.column(key)).toValue(data.get(key));
        }

        GeneralInsertStatementProvider insertStatement = generalInsertDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.debug("SQL - {}", insertStatement.getInsertStatement());
        log.debug("SQL - row - {}", insertStatement.getParameters());

        int rows = 0;

        try {
            rows = namedParameterJdbcTemplate.update(insertStatement.getInsertStatement(), insertStatement.getParameters());
        }
        catch(DataAccessException e) {
            throw new GenericDataAccessException(e.getMessage());
        }
        log.debug("Inserted - {} row(s)", rows);

        return rows;

    }

    private void processTSID(Map<String, Object> data, String tsid, String tsidType) {
        //1. check if tsid column is specified if yes go ahead add or update it with generated TSID value

        if(StringUtils.isNotBlank(tsid)) {
            data.put(tsid, getTSIDValue(tsidType));
        }

        // adding to data will ensure its added in select statement

    }

    private Object getTSIDValue(String tsidType) {
        if(StringUtils.equalsAnyIgnoreCase(tsidType, "number", "string")) {
            return
            StringUtils.equalsIgnoreCase(tsidType, "number") ? TSID.Factory.getTsid().toLong() :
                    TSID.Factory.getTsid().toString();


        }

        throw new GenericDataAccessException("Invalid TSID data type.");

    }

    @Transactional
    public int[] saveBulk(String schemaName, String tableName, List<Map<String, Object>> dataList) {
        if(Objects.isNull(dataList) || dataList.isEmpty()) throw new RuntimeException("No data provided");

        SqlTable table = SqlTable.of(tableName);

        Map<String,Object> item = dataList.get(0);

        BatchInsertDSL<Map<String, Object>> batchInsertDSL = insert(dataList)
                .into(table);

        for(String key : item.keySet()) {
            batchInsertDSL.map(table.column(key)).toProperty(key);
        }

        BatchInsert<Map<String,Object>> batchInsert =
                batchInsertDSL
                .build()
                .render(db2RestRenderingStrategy);

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        log.debug("SQL -> {}", batchInsert.getInsertStatementSQL());
        log.debug("batch -> {}", batch);

        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(batchInsert.getInsertStatementSQL(), batch);

        log.debug("Update counts - {}", updateCounts.length);

        return updateCounts;
    }



}
