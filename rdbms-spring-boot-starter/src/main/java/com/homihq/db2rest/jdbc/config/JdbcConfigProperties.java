package com.homihq.db2rest.jdbc.config;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db2rest.jdbc")
@Validated
@Slf4j
public class JdbcConfigProperties {

    private boolean allowSafeDelete;
    private int defaultFetchLimit;

    private List<String> includeSchemas;



}
