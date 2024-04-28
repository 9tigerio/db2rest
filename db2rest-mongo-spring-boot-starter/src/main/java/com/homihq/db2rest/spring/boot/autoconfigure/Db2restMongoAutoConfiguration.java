package com.homihq.db2rest.spring.boot.autoconfigure;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@EnableConfigurationProperties(Db2RestConfigProperties.class)
@ConditionalOnClass(SimpleMongoClientDatabaseFactory.class)
public class Db2restMongoAutoConfiguration {

}
