package com.homihq.db2rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db2rest")
public class Db2RestConfigProperties {

    private boolean allowUnsafeDelete;
}
