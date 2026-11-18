package com.homihq.db2rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.provider.apikey.ApiKeyAuthProvider;
import com.homihq.db2rest.auth.provider.basic.BasicAuthProvider;
import com.homihq.db2rest.auth.provider.AbstractAuthProvider;
import com.homihq.db2rest.auth.datalookup.AuthDataLookup;
import com.homihq.db2rest.auth.datalookup.ApiAuthDataLookup;
import com.homihq.db2rest.auth.datalookup.AuthDataProperties;
import com.homihq.db2rest.auth.datalookup.FileAuthDataLookup;
import com.homihq.db2rest.auth.datalookup.NoAuthdataLookup;
import com.homihq.db2rest.auth.provider.jwt.JwtAuthProvider;
import com.homihq.db2rest.auth.provider.jwt.JwtProperties;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "db2rest.auth", name = "enabled", havingValue = "true")
public class AuthConfiguration {

    @Bean("authAntPathMatcher")
    public AntPathMatcher authAntPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public AuthFilter authFilter(
            AbstractAuthProvider authProvider,
            ObjectMapper objectMapper
    ) {
        log.info("** Auth enabled. Initializing auth components.");

        return new AuthFilter(authProvider, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "db2rest.auth", name = "provider", havingValue = "apiKey")
    public AbstractAuthProvider apiKeyAuthProvider(AuthDataProperties authDataProperties) {
        return new ApiKeyAuthProvider(authDataProvider(authDataProperties), authAntPathMatcher());
    }

    @Bean
    @ConditionalOnProperty(prefix = "db2rest.auth", name = "provider", havingValue = "basic")
    public AbstractAuthProvider basicAuthProvider(AuthDataProperties authDataProperties) {
        return new BasicAuthProvider(authDataProvider(authDataProperties), authAntPathMatcher());
    }

    @Bean
    @ConditionalOnProperty(prefix = "db2rest.auth", name = "provider", havingValue = "jwt")
    public AbstractAuthProvider jwtAuthProvider(
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor,
            AuthDataProperties authDataProperties,
            JwtProperties jwtProperties
    ) {
        return new JwtAuthProvider(authDataProvider(authDataProperties), authAntPathMatcher(),
                jwtProcessor, jwtProperties.getRolesNamespace());
    }

    @Bean
    @ConditionalOnProperty(prefix = "db2rest.auth", name = "provider", havingValue = "jwt")
    public ConfigurableJWTProcessor<SecurityContext> jwtProcessor(JwtProperties jwtProperties) throws MalformedURLException {
        JWKSource<SecurityContext> keySource = jwtProperties.getKey() != null
                ? new ImmutableSecret<>(jwtProperties.getKey())
                : JWKSourceBuilder
                .create(new URL(jwtProperties.getJwksUrl()))
                .retrying(true)
                .build();

        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        jwtProcessor.setJWSTypeVerifier(
                new DefaultJOSEObjectTypeVerifier<>(
                        new JOSEObjectType("at+jwt"),
                        new JOSEObjectType("JWT")));

        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                jwtProperties.getAlgorithm(),
                keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        return jwtProcessor;
    }

    @Bean
    public AuthDataLookup authDataProvider(AuthDataProperties authDataProperties) {

        if (authDataProperties.isFileProvider()) {
            log.info("Initializing file auth data provider");
            return new FileAuthDataLookup(authDataProperties.getSource());
        } else if (authDataProperties.isApiDataProvider()) {
            log.info("Initializing API auth data provider");
            return new ApiAuthDataLookup(authDataProperties.getApiEndpoint(), authDataProperties.getApiKey());
        }
        log.info("No auth data provider");
        return new NoAuthdataLookup();
    }

}
