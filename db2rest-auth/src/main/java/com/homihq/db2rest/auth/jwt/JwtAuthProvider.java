package com.homihq.db2rest.auth.jwt;

import com.homihq.db2rest.auth.common.AbstractAuthProvider;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.UserDetail;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthProvider extends AbstractAuthProvider {

    private static final String BEARER_AUTH = "Bearer";

    private final AuthDataProvider authDataProvider;
    private final AntPathMatcher antPathMatcher;
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @Override
    public boolean canHandle(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BEARER_AUTH);
    }

    @Override
    public UserDetail authenticate(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        String token = StringUtils.replace(authHeader, String.format("%s ", BEARER_AUTH), "", 1);

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return new UserDetail(claimsSet.getSubject(), List.of());
        } catch (ParseException | BadJOSEException | JOSEException e) {
            log.error("Error in JWT validation - ", e);
        }

        return null;
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {
        return super.authorizeInternal(userDetail, requestUri, method, authDataProvider.getApiResourceRoles(), antPathMatcher);
    }

    @Override
    public boolean isExcluded(String requestUri, String method) {
        return false;
    }
}
