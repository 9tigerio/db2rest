package com.homihq.db2rest;


import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(MongoContainerConfiguration.class)
@ActiveProfiles("it-mongo")
@SuppressWarnings("java:S2187")
public class MongoBaseIntegrationTest extends BaseIntegrationTest {

}
