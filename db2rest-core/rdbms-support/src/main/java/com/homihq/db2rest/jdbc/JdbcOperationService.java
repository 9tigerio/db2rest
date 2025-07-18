package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.ArrayTypeValueHolder;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.core.SimpleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class JdbcOperationService implements DbOperationService {


    @Override
    public int update(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    @Override
    public List<Map<String, Object>> read(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql,
                                          Dialect dialect) {
        return namedParameterJdbcTemplate
                .query(sql, new MapSqlParameterSource(paramMap), new SimpleRowMapper(dialect));
    }

    @Override
    public Map<String, Object> findOne(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql, Map<String, Object> paramMap) {
        return namedParameterJdbcTemplate.queryForMap(sql, paramMap);
    }

    @Override
    public ExistsResponse exists(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        List<String> queryResult = namedParameterJdbcTemplate.query(sql,
                paramMap,
                (rs, rowNum) -> rs.getString(1)
        );

        return queryResult.isEmpty()
                ? new ExistsResponse(false)
                : new ExistsResponse(true);
    }

    @Override
    public CountResponse count(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql) {
        Long itemCount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Long.class);
        return new CountResponse(itemCount);
    }

    @Override
    public Object queryCustom(NamedParameterJdbcTemplate namedParameterJdbcTemplate, boolean single, String sql, Map<String, Object> params) {
        return single ?
                namedParameterJdbcTemplate.queryForMap(sql, params) :
                namedParameterJdbcTemplate.queryForList(sql, params);
    }

    @Override
    public int delete(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            Map<String, Object> params, String sql) {
        return namedParameterJdbcTemplate.update(sql,
                params);
    }

    @Override
    public CreateResponse create(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            Map<String, Object> data, String sql, DbTable dbTable) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        for (String key : data.keySet()) {

            Object value = data.get(key);

            if (value instanceof ArrayTypeValueHolder val) {
                value = processArrayValue(namedParameterJdbcTemplate, val);
            }

            parameterSource.addValue(key, value);
        }

        int row = namedParameterJdbcTemplate.update(sql, parameterSource,
                keyHolder, dbTable.getKeyColumnNames()
        );

        log.info("*** update fired returning ***");
        
        // Handle SQLite-specific key mapping
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.containsKey("last_insert_rowid()")) {
            // SQLite returns last_insert_rowid() - map to actual column name
            String[] keyColumns = dbTable.getKeyColumnNames();
            if (keyColumns.length == 1) {
                Object keyValue = keys.get("last_insert_rowid()");
                keys = Map.of(keyColumns[0], keyValue);
            }
        }
        
        return new CreateResponse(row, keys);
    }

    private Array processArrayValue(NamedParameterJdbcTemplate namedParameterJdbcTemplate, ArrayTypeValueHolder val) {

        try {
            Connection connection =
                    namedParameterJdbcTemplate.getJdbcTemplate().getDataSource().getConnection();

            log.info("connection - {}", connection);

            return connection.createArrayOf(val.sqlType(), val.values());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new GenericDataAccessException("Unable to convert Array field");
        }
    }

    @Override
    public CreateBulkResponse batchUpdate(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            List<Map<String, Object>> dataList, String sql, DbTable dbTable) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        int[] updateCounts;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        updateCounts =
                namedParameterJdbcTemplate.batchUpdate(sql, batch, keyHolder, dbTable.getKeyColumnNames());

        return new CreateBulkResponse(updateCounts, keyHolder.getKeyList());
    }


    public CreateBulkResponse batchUpdate(
            NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            List<Map<String, Object>> dataList, String sql) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        int[] updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batch);

        return new CreateBulkResponse(updateCounts, null);
    }
}
