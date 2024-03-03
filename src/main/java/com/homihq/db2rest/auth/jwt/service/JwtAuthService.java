package com.homihq.db2rest.auth.jwt.service;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthService {


    private final JWTVerifier jwtVerifier;

    public boolean validateToken(String token) {

        try {
            jwtVerifier.verify(token);
        } catch (final JWTVerificationException verificationException) {
            log.warn("token invalid: {}", verificationException.getMessage());
            return false;
        }

        return true;
    }
}
