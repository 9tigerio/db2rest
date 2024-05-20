package com.homihq.db2rest.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.common.AbstractAuthProvider;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.data.ApiAuthDataProvider;
import com.homihq.db2rest.auth.data.AuthDataProperties;
import com.homihq.db2rest.auth.data.FileAuthDataProvider;
import com.homihq.db2rest.auth.data.NoAuthdataProvider;
import com.homihq.db2rest.auth.jwt.JwtAuthProvider;
import com.homihq.db2rest.auth.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "db2rest.auth", name="enabled" , havingValue = "true")
public class AuthConfiguration {

    private final JwtProperties jwtProperties;


    @Bean("authAntPathMatcher")
    public AntPathMatcher authAntPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public AuthFilter authFilter(AuthDataProperties authDataProperties, ObjectMapper objectMapper) {
        log.info("** Auth enabled. Initializing auth components.");
        List<AbstractAuthProvider> authProviders = List.of(jwtAuthProvider(authDataProperties));

        return new AuthFilter(authProviders, objectMapper);
    }

    @Bean
    public JwtAuthProvider jwtAuthProvider(AuthDataProperties authDataProperties) {

        JWTVerifier jwtVerifier =
        JWT.require(jwtProperties.getAlgo())
                .withIssuer(jwtProperties.getIssuers())
                .build();

        return new JwtAuthProvider(jwtVerifier,
                authDataProvider(authDataProperties), authAntPathMatcher());
    }

    @Bean
    public AuthDataProvider authDataProvider(AuthDataProperties authDataProperties) {

        if(authDataProperties.isFileProvider()) {
            log.info("Initializing file auth data provider");
            return new FileAuthDataProvider(authDataProperties.getFile());
        }
        else if(authDataProperties.isApiDataProvider()){
            log.info("Initializing API auth data provider");
            return new ApiAuthDataProvider(authDataProperties.getApiEndpoint(), authDataProperties.getApiKey());
        }
        log.info("No auth data provider");
        return new NoAuthdataProvider();
    }

}
