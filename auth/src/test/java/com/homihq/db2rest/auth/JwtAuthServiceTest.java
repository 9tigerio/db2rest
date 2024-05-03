package com.homihq.db2rest.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.homihq.db2rest.auth.jwt.service.JwtAuthService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Disabled
public class JwtAuthServiceTest  {


    private JwtAuthService jwtAuthService;

    @Value("${db2rest.jwt.secret}")
    private String jwtSecret;

    @Test
    @DisplayName("Test jwt token verification")
    public void testJwtTokenVerification() {
        String token = JWT.create()
                .withIssuer("db2rest-test")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000))
                .sign(Algorithm.HMAC512(jwtSecret));

        assert jwtAuthService.isValidToken(token);
    }
}
