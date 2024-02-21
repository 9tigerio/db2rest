package com.homihq.db2rest.auth;

import com.homihq.db2rest.auth.service.ApiKeyVerifierService;
import com.homihq.db2rest.auth.to.AuthInfo;
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


    private void setAuthContextForToken(String token) {
        if(StringUtils.isBlank(token))
            return;
        AuthInfo authInfo = apiKeyVerifier.getAuthInfo(token);
        if(authInfo != null)
            logger.info(" **** api key is verified");
        else
            logger.info("**** api key is not verified");
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
            setAuthContextForToken(request.getHeader(API_KEY_HEADER));
        }
        filterChain.doFilter(request, response);
    }
}
