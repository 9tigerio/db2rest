package com.homihq.db2rest.auth.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.homihq.db2rest.auth.common.*;
import com.homihq.db2rest.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthProvider extends AbstractAuthProvider {

    private final JWTVerifier jwtVerifier;
    private final AuthDataProvider authDataProvider;

    @Override
    public boolean canHandle(String authHeader) {
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ");
    }

    @Override
    public UserDetail authenticate(String authHeader) {
        String token = StringUtils.replace(authHeader, "Bearer ", "", 1);
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            return new UserDetail(decodedJWT.getSubject(), List.of());
        }
        catch (JWTVerificationException e) {
            throw new AuthException("Error in JWT validation - " +  e.getMessage());
        }
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {
        AntPathMatcher antPathMatcher = new AntPathMatcher();

        //resource mapping
        Optional<ResourceRole> resourceRole =
        authDataProvider.getApiResourceRoles()
                .stream()
                .filter(r -> antPathMatcher.match(r.resource(), requestUri))
                .findFirst();

        //resource to role mapping
        //TODO - method check required
        if(resourceRole.isPresent()) {
            ResourceRole rr = resourceRole.get();
            boolean roleMatch =
            rr.roles()
                    .stream()
                    .anyMatch(role -> StringUtils.equalsAnyIgnoreCase(role, userDetail.getRoles()));

            log.info("Role match - {}", roleMatch);

        }

        return false;
    }
}
