package com.homihq.db2rest.spring.boot.autoconfigure;


import com.homihq.db2rest.jdbc.config.JdbcConfigProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JdbcConfigProperties.class)
public class Db2restAutoConfiguration {

}
