package com.homihq.db2rest.rest.service;

import com.homihq.db2rest.Db2RestConfigProperties;
import com.homihq.db2rest.error.DeleteOpNotAllowedException;
import com.homihq.db2rest.rsql.FilterBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteService {

    private final JdbcTemplate jdbcTemplate;
    private final FilterBuilderService filterBuilderService;

    private final Db2RestConfigProperties db2RestConfigProperties;

    @Transactional
    public void delete(String tableName, String rSql) {
        String sql = "DELETE FROM " + tableName ;

        if(StringUtils.isBlank(rSql) && !db2RestConfigProperties.isAllowUnsafeDelete()) {
            throw new DeleteOpNotAllowedException(false);
        }
        else{
            sql = sql + " WHERE " + filterBuilderService.getWhereClause(tableName , rSql);
        }


        log.info("sql - {}", sql);
        jdbcTemplate.update(sql); //TODO use PSTMT ??



    }
}
