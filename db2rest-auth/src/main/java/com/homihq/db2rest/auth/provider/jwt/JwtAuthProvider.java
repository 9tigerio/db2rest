package com.homihq.db2rest.auth.provider.jwt;

import com.homihq.db2rest.auth.data.UserDetail;
import com.homihq.db2rest.auth.datalookup.AuthDataLookup;
import com.homihq.db2rest.auth.provider.AbstractAuthProvider;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

@Slf4j
public class JwtAuthProvider extends AbstractAuthProvider {
    private static final String BEARER_AUTH = "Bearer";
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private final String rolesNamespace;

    public JwtAuthProvider(AuthDataLookup authDataLookup, AntPathMatcher antPathMatcher,
                           ConfigurableJWTProcessor<SecurityContext> jwtProcessor, String rolesNamespace) {
        super(authDataLookup, antPathMatcher);
        this.jwtProcessor = jwtProcessor;
        this.rolesNamespace = rolesNamespace;
    }

    @Override
    public boolean canHandle(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER_AUTH);
    }

    @Override
    public UserDetail authenticate(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith(BEARER_AUTH + " ")) {
            log.debug("Missing or non-Bearer Authorization header");
            return null;
        }
        String token = authHeader.substring(BEARER_AUTH.length() + 1).trim();

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return new UserDetail(claimsSet.getSubject(), extractRoles(claimsSet));
        } catch (ParseException | BadJOSEException | JOSEException e) {
            log.error("Error in JWT validation - ", e);
        }

        return null;
    }

    private List<String> extractRoles(JWTClaimsSet claimsSet) {
        if (StringUtils.isBlank(rolesNamespace)) {
            return List.of();
        }

        Object claim = claimsSet.getClaim(rolesNamespace);
        if (claim == null) {
            return List.of();
        }
        if (claim instanceof String role) {
            return List.of(role);
        }
        if (claim instanceof Collection<?> values) {
            return values.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        log.warn("JWT claim '{}' is not a string or array of strings; ignoring roles", rolesNamespace);
        return List.of();
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {
        return super.authorizeInternal(userDetail, requestUri, method, authDataLookup.getApiResourceRoles(), antPathMatcher);
    }

    @Override
    public boolean isExcluded(String requestUri, String method) {
        return super.isExcludedInternal(requestUri, method, authDataLookup.getExcludedResources(), antPathMatcher);
    }
}
