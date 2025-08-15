package com.homihq.db2rest.rest.mssql;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.BaseIntegrationTest;
import com.homihq.db2rest.MsSQLServerContainerConfiguration;
import io.hosuaby.inject.resources.junit.jupiter.WithJacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@Import(MsSQLServerContainerConfiguration.class)
@ActiveProfiles("it-mssql")
class MsSQLBaseIntegrationTest extends BaseIntegrationTest {

    protected static final String TEST_JSON_FOLDER = "/testdata";
    protected static final String DB_NAME = "mssql";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @WithJacksonMapper
    protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected String getPrefixApiUrl() {
        return VERSION + "/" + DB_NAME;
    }

    protected boolean deleteRow(String table, String column, int id) {
        var query = "DELETE FROM " + table + " WHERE " + column + " = ?";
        return jdbcTemplate.update(query, id) == 1;
    }
}
