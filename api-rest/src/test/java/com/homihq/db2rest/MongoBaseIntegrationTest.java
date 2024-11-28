package com.homihq.db2rest;


import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(MongoContainerConfiguration.class)
@ActiveProfiles("it-mongo")
public class MongoBaseIntegrationTest extends BaseIntegrationTest {

}
