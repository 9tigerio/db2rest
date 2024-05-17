package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.homihq.db2rest.auth.common.AuthProvider;
import com.homihq.db2rest.auth.common.Subject;
import com.homihq.db2rest.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthProvider implements AuthProvider {

    private final JWTVerifier jwtVerifier;

    @Override
    public boolean canHandle(String authHeader) {
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ");
    }

    @Override
    public Subject handle(String authHeader) {
        String token = StringUtils.replace(authHeader, "Bearer ", "", 1);
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            return new Subject(decodedJWT.getSubject(), List.of(), "");
        }
        catch (JWTVerificationException e) {
            throw new AuthException("Error in JWT validation - " +  e.getMessage());
        }
    }
}
