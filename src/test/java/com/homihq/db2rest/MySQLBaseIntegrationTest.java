package com.homihq.db2rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@Import(MySQLContainerConfiguration.class)
public class MySQLBaseIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected boolean deleteRow(String table, String column,  int id) {
        var query = "DELETE FROM " + table + " WHERE " + column + " = ?";
        return jdbcTemplate.update(query, id) == 1;
    }
}
