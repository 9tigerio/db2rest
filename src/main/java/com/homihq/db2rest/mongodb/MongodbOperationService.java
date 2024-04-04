package com.homihq.db2rest.mongodb;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import com.homihq.db2rest.rest.read.dto.CountResponse;
import com.homihq.db2rest.rest.read.dto.ExistsResponse;

import java.util.List;
import java.util.Map;

public interface MongodbOperationService extends DbOperationService {

    @Override
    default int update(Map<String, Object> paramMap, String sql) {
        return 0;
    }

    @Override
    default List<Map<String, Object>> read(Map<String, Object> paramMap, String sql) {
        return List.of();
    }

    @Override
    default Map<String, Object> findOne(String sql, Map<String, Object> paramMap) {
        return Map.of();
    }

    @Override
    default ExistsResponse exists(Map<String, Object> paramMap, String sql) {
        return null;
    }

    @Override
    default CountResponse count(Map<String, Object> paramMap, String sql) {
        return null;
    }

    @Override
    default Object queryCustom(boolean single, String sql, Map<String, Object> params) {
        return null;
    }

    @Override
    default int delete(Map<String, Object> params, String sql) {
        return 0;
    }

    @Override
    default CreateResponse create(Map<String, Object> data, String sql, DbTable dbTable) {
        return null;
    }

    @Override
    default CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql, DbTable dbTable) {
        return null;
    }

    @Override
    default CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql) {
        return null;
    }
}
