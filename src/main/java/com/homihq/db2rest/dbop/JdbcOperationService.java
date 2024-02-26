package com.homihq.db2rest.dbop;

import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateResponse;
import com.homihq.db2rest.rest.delete.dto.DeleteContext;
import com.homihq.db2rest.rest.read.dto.CountResponse;
import com.homihq.db2rest.rest.read.dto.ExistsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JdbcOperationService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int update(Map<String, Object> paramMap, String sql) {
        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    public List<Map<String, Object>> read(Map<String, Object> paramMap, String sql) {
        int hashCode = this.namedParameterJdbcTemplate.getJdbcTemplate().getDataSource().hashCode();
        log.info("**** hashcode - {}", hashCode);

        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }

    public Map<String, Object> findOne(String sql, Map<String, Object> paramMap) {
        return namedParameterJdbcTemplate.queryForMap(sql, paramMap);
    }

    public ExistsResponse exists(Map<String, Object> paramMap, String sql) {
        List<String> queryResult = namedParameterJdbcTemplate.query(sql,
                paramMap,
                (rs, rowNum) -> rs.getString(1)
        );

        if (queryResult.isEmpty()) return new ExistsResponse(false);
        return new ExistsResponse(true);
    }

    public CountResponse count(Map<String, Object> paramMap, String sql) {
        Long itemCount = namedParameterJdbcTemplate.queryForObject(sql, paramMap, Long.class);
        return new CountResponse(itemCount);
    }

    public Object queryCustom(boolean single, String sql, Map<String, Object> params) {
        return single ?
                namedParameterJdbcTemplate.queryForMap(sql, params) :
                namedParameterJdbcTemplate.queryForList(sql, params);
    }

    public int delete(Map<String, Object> params, String sql) {
        return namedParameterJdbcTemplate.update(sql,
                params);
    }

    public CreateResponse create(Map<String, Object> data, String sql, DbTable dbTable) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int row = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(data),
                keyHolder, dbTable.getKeyColumnNames()
        );


        return new CreateResponse(row, keyHolder.getKeys());
    }

    public CreateBulkResponse batchUpdate(List<Map<String, Object>> dataList, String sql, DbTable dbTable) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

        int[] updateCounts;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batch, keyHolder, dbTable.getKeyColumnNames());

        return new CreateBulkResponse(updateCounts, keyHolder.getKeyList());
    }
}
