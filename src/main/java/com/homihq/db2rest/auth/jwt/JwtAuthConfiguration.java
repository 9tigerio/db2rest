package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.jwt.service.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthConfiguration {

    @Value("${db2rest.jwt.secret}")
    private String jwtSecretKey;

    @Bean
    public JWTVerifier jwtVerifier() {
        return JWT.require(Algorithm.HMAC512(jwtSecretKey)).build();
    }

    @Bean
    public JwtAuthService jwtAuthService(JWTVerifier verifier) {
        return new JwtAuthService(verifier);
    }
    @Bean
    public JwtAuthFilter jwtAuthFilter(JWTVerifier verifier, ObjectMapper objectMapper) {
        return new JwtAuthFilter(jwtAuthService(verifier), objectMapper);
    }
}
