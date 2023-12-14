package com.homihq.db2rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueryService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public Object find(String tableName, List<String> columns) {

        String sql = "SELECT * from " + tableName;

        return jdbcTemplate.queryForList(sql);
    }
}
