package com.homihq.db2rest;

import org.springframework.context.annotation.Import;

@Import(MySQLContainerConfiguration.class)
public class MySQLBaseIntegrationTest extends BaseIntegrationTest{

}
