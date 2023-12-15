package com.homihq.db2rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void save(String tableName, Map<String,Object> data) {
        String sql = "INSERT INTO " + tableName +
                " ( " + getColumns(data) + " ) VALUES (" + getPlaceholders(data) + " ) ";

        log.info("sql - {}", sql);

        jdbcTemplate.update(sql, getValues(data));
    }

    private Object[] getValues(Map<String, Object> data) {
        return  data.values().toArray();
    }

    private String getPlaceholders(Map<String, Object> data) {

        List<String> positional = Collections.nCopies(data.keySet().size(), "?");

        return String.join(" , " , positional);
    }

    private String getColumns(Map<String, Object> data) {
        Set<String> keys = data.keySet();


        return String.join(" , " , keys);

    }


}
