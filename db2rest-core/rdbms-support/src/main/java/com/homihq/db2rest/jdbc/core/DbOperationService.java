package com.homihq.db2rest.jdbc.core;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.ExistsResponse;
import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbTable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;

public interface DbOperationService {
    int update(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql);

    List<Map<String, Object>> read(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                   Map<String, Object> paramMap, String sql,
                                   Dialect dialect);

    Map<String, Object> findOne(NamedParameterJdbcTemplate namedParameterJdbcTemplate, String sql, Map<String, Object> paramMap);

    ExistsResponse exists(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql);

    CountResponse count(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> paramMap, String sql);

    Object queryCustom(NamedParameterJdbcTemplate namedParameterJdbcTemplate, boolean single, String sql, Map<String, Object> params);

    int delete(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> params, String sql);

    CreateResponse create(NamedParameterJdbcTemplate namedParameterJdbcTemplate, Map<String, Object> data, String sql, DbTable dbTable);

    CreateBulkResponse batchUpdate(NamedParameterJdbcTemplate namedParameterJdbcTemplate, List<Map<String, Object>> dataList, String sql, DbTable dbTable);

    CreateBulkResponse batchUpdate(NamedParameterJdbcTemplate namedParameterJdbcTemplate, List<Map<String, Object>> dataList, String sql);
}
