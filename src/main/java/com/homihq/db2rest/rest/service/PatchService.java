package com.homihq.db2rest.rest.service;

import com.homihq.db2rest.rsql.FilterBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatchService {

    private final JdbcTemplate jdbcTemplate;
    private final FilterBuilderService filterBuilderService;

    @Transactional
    public void patch(String tableName, Map<String,Object> data, String rSql) {
        String sql = "UPDATE " + tableName +
                " SET " + getUpdatedColumns(data) ;

        if(StringUtils.isNotBlank(rSql)) {
            sql = sql + " WHERE " + filterBuilderService.getWhereClause(tableName , rSql);
        }

        log.info("sql - {}", sql);
        jdbcTemplate.update(sql , getValues(data)); //TODO use PSTMT ??



    }

    private Object[] getValues(Map<String, Object> data) {
        return  data.values().toArray();
    }

    private String getUpdatedColumns(Map<String, Object> data) {
        return data.keySet().stream()
                .map(key -> key + " = ? " )
                .collect(Collectors.joining(", ", "", ""));

    }
}
