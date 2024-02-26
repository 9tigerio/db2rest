package com.homihq.db2rest.dbop;

import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import com.homihq.db2rest.rest.read.dto.CountResponse;
import com.homihq.db2rest.rest.read.dto.ExistsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "d1")
public class D1OperationService implements DbOperationService{
    @Override
    public int update(Map<String, Object> paramMap, String sql) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> read(Map<String, Object> paramMap, String sql) {
        return null;
    }

    @Override
    public Map<String, Object> findOne(String sql, Map<String, Object> paramMap) {
        return null;
    }

    @Override
    public ExistsResponse exists(Map<String, Object> paramMap, String sql) {
        return null;
    }

    @Override
    public CountResponse count(Map<String, Object> paramMap, String sql) {
        return null;
    }

    @Override
    public Object queryCustom(boolean single, String sql, Map<String, Object> params) {
        return null;
    }

    @Override
    public int delete(Map<String, Object> params, String sql) {
        return 0;
    }

    @Override
    public CreateResponse create(Map<String, Object> data, String sql, DbTable dbTable) {
        return null;
    }

    @Override
    public CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql, DbTable dbTable) {
        return null;
    }
}
