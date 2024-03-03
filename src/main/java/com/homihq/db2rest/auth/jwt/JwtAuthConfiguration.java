package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.jwt.service.JwtAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthConfiguration {

    @Bean
    public JwtAuthService jwtAuthService(JWTVerifier verifier) {
        return new JwtAuthService(verifier);
    }
    @Bean
    public JwtAuthFilter jwtAuthFilter(JWTVerifier verifier, ObjectMapper objectMapper) {
        return new JwtAuthFilter(jwtAuthService(verifier), objectMapper);
    }
}
