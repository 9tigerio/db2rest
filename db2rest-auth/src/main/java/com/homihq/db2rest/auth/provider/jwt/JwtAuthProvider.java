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
        String token = StringUtils.replace(authHeader, String.format("%s ", BEARER_AUTH), "", 1);

        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            List<String> roles = List.of();
            if (rolesNamespace != null) {
                List<String> claimsRoles = claimsSet.getStringListClaim(rolesNamespace);
                if (claimsRoles != null) {
                    roles = claimsRoles;
                }
            }
            return new UserDetail(claimsSet.getSubject(), roles);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            log.error("Error in JWT validation - ", e);
        }

        return null;
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {
        return super.authorizeInternal(userDetail, requestUri, method, authDataLookup.getApiResourceRoles(), antPathMatcher);
    }

    @Override
    public boolean isExcluded(String requestUri, String method) {
        return false;
    }
}
