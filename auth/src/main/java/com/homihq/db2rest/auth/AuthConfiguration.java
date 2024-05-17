package com.homihq.db2rest.auth;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.homihq.db2rest.auth.jwt.JwtAuthProvider;
import com.homihq.db2rest.auth.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "db2rest.auth", name="type" , havingValue = "jwt")
public class AuthConfiguration {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtAuthProvider jwtAuthProvider() {
        JWTVerifier jwtVerifier =
        JWT.require(jwtProperties.getAlgo())
                .withIssuer(jwtProperties.getIssuers())
                .build();

        return new JwtAuthProvider(jwtVerifier);
    }

}
