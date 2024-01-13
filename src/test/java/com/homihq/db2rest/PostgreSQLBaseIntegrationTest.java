package com.homihq.db2rest;

import org.springframework.context.annotation.Import;

@Import(PostgreSQLContainerConfiguration.class)
public class PostgreSQLBaseIntegrationTest extends BaseIntegrationTest{

}
