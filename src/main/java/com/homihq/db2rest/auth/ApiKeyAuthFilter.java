package com.homihq.db2rest.auth;

import com.homihq.db2rest.auth.service.ApiKeyVerifierService;
import com.homihq.db2rest.auth.to.AuthInfo;
import com.homihq.db2rest.exception.AuthenticationFailedException;
import com.homihq.db2rest.exception.MissingAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyVerifierService apiKeyVerifier;

    String[] WHITE_LIST_URLS = {};
    String API_KEY_HEADER = "X-API-KEY";


    private void setAuthContext(String token) {

        // Get key information from unkey.dev
        AuthInfo authInfo = apiKeyVerifier.getAuthInfo(token);

        // Check if able to get key information
        if(authInfo == null) {
            throw new AuthenticationFailedException("Unable to get key status");
        }

        // Check if key is valid
        if(!apiKeyVerifier.isKeyValid(authInfo)) {
            throw new AuthenticationFailedException("Unable to verify key. ERR CODE: " + authInfo.code);
        }

        AuthContext.setCurrentAuthInfo(authInfo);
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();

        if(StringUtils.containsAnyIgnoreCase(url, WHITE_LIST_URLS)) {
            logger.info("*** Bypass due to white list URL: " + url);
        } else {
            logger.info(" *** Apply ApiKeyAuth Filter ***");

            // Check the header and return authentication failed if header not present
            String token = request.getHeader(API_KEY_HEADER);
            if(StringUtils.isBlank(token)) {
                throw new MissingAuthenticationException("Missing header " + API_KEY_HEADER);
            }

            setAuthContext(request.getHeader(API_KEY_HEADER));
        }
        filterChain.doFilter(request, response);
    }
}
