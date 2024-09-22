package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.BaseIntegrationTest;
import com.homihq.db2rest.MsSQLServerContainerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@Import(MsSQLServerContainerConfiguration.class)
@ActiveProfiles("it-mssql")
class MsSQLBaseIntegrationTest extends BaseIntegrationTest {

    protected static final String TEST_JSON_FOLDER = "/testdata";
    protected static final String DB_NAME = "mssql";

    @WithJacksonMapper
    protected ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    protected String getPrefixApiUrl() {
        return VERSION + "/" + DB_NAME;
    }
}
