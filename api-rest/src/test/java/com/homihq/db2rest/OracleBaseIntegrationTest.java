package com.homihq.db2rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@Import(MySQLContainerConfiguration.class)
@ActiveProfiles("it-oracle")
public class OracleBaseIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected boolean deleteRow(String table, String column,  int id) {
        var query = "DELETE FROM " + table + " WHERE " + column + " = ?";
        return jdbcTemplate.update(query, id) == 1;
    }

//    @Test
//    protected void checkTimestampFormat(){
//        String formatSql = "SELECT value FROM v$nls_parameters WHERE parameter = 'NLS_TIMESTAMP_FORMAT'";
//        String timestampFormat = jdbcTemplate.queryForObject(formatSql, String.class);
//        System.out.println("Current NLS_TIMESTAMP_FORMAT: " + timestampFormat);
//    }
}
