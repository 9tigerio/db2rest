package com.homihq.db2rest.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "db2rest.auth.jwt")
@Validated
@Slf4j
@Getter
@Setter
public class JwtProperties { //Currently support HMAC only

    String algorithm;
    String secret;

    String [] issuers;

    String envPrivateKey;
    String envPublicKey;

    String privateKeyFile;
    String publicKeyFile;

    public boolean isEnvironmentBasedProperty() {
        return StringUtils.isBlank(envPrivateKey) && StringUtils.isBlank(envPublicKey);
    }

    public boolean isFileBasedProperty() {
        return StringUtils.isBlank(privateKeyFile) && StringUtils.isBlank(publicKeyFile);
    }

}
