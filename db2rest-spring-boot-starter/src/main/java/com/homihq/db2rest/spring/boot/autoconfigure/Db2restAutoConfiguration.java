package com.homihq.db2rest.spring.boot.autoconfigure;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Db2RestConfigProperties.class)
public class Db2restAutoConfiguration {

}
