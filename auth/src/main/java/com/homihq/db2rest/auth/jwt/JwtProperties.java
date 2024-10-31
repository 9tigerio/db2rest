package com.homihq.db2rest.auth.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;

@Configuration
@ConfigurationProperties(prefix = "db2rest.auth.jwt")
@ConditionalOnProperty(prefix = "db2rest.auth", name = "provider", havingValue = "jwt")
@Validated
@Data
public class JwtProperties {

    @NotNull
    private JWSAlgorithm algorithm;

    @AssertTrue(message = "Key or jwksUrl must be present!")
    public boolean isKeyOrJwksUrlPresent() {
        return this.getJwksUrl() != null || this.getKey() != null;
    }

    private SecretKey key;

    private String jwksUrl;

    public void setAlgorithm(String algorithm) {
        this.algorithm = JWSAlgorithm.parse(algorithm);
    }

    public void setKey(String secret) {
        var jwk = new OctetSequenceKey.Builder(secret.getBytes())
                .algorithm(algorithm)
                .build();

        this.key = jwk.toSecretKey();
    }

}
