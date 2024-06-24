package com.homihq.db2rest.auth.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "db2rest.auth.data")
@Validated
@Slf4j
@Getter
@Setter
public class AuthDataProperties {

    String source;

    String apiEndpoint;
    String apiKey;


    public boolean isFileProvider() {
        return StringUtils.startsWith(source, "file");
    }

    public boolean isApiDataProvider() {return StringUtils.startsWithAny(source, "http", "https");}
}
