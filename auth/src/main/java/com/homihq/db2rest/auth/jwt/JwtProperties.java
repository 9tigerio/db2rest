package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    public Algorithm getAlgo() {
        switch (algorithm) {
            case "HMAC256" -> {
                return Algorithm.HMAC256(secret);
            }
            case "HMAC384" -> {
                return Algorithm.HMAC384(secret);
            }
            case "HMAC512" -> {
                return Algorithm.HMAC512(secret);
            }
            default -> throw new RuntimeException(algorithm + " is not supported.");
        }
    }

}
