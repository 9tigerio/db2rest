package com.homihq.db2rest.jdbc.config.core;

import com.homihq.db2rest.jdbc.config.dto.CountResponse;
import com.homihq.db2rest.jdbc.config.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;

import java.util.List;
import java.util.Map;

public interface DbOperationService {
    int update(Map<String, Object> paramMap, String sql);

    List<Map<String, Object>> read(Map<String, Object> paramMap, String sql);

    Map<String, Object> findOne(String sql, Map<String, Object> paramMap);

    ExistsResponse exists(Map<String, Object> paramMap, String sql);

    CountResponse count(Map<String, Object> paramMap, String sql);

    Object queryCustom(boolean single, String sql, Map<String, Object> params);

    int delete(Map<String, Object> params, String sql);

    CreateResponse create(Map<String, Object> data, String sql, DbTable dbTable);

    CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql, DbTable dbTable);

    CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql);

    default CreateResponse create(Map<String, Object> data, String collectionName) {
        return null;
    }
}
