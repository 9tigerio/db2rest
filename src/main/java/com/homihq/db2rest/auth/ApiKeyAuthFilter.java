package com.homihq.db2rest.auth;

import com.homihq.db2rest.auth.service.ApiKeyVerifierService;
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


    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();

        if(StringUtils.containsAnyIgnoreCase(url, WHITE_LIST_URLS)) {
            logger.info("Bypass due to white list URL: " + url);
        } else {
            logger.info(" *** Apply ApiKeyAuth Filter ***");

            final String token = request.getHeader(API_KEY_HEADER);
            if(StringUtils.isBlank(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // TODO: Might need to obtain the user??
            if(!apiKeyVerifier.verifyApiKey(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // TODO: Set AuthContext

        }
        filterChain.doFilter(request, response);
    }
}
