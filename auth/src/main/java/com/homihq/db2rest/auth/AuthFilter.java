package com.homihq.db2rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.common.AbstractAuthProvider;
import com.homihq.db2rest.auth.common.UserDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final AbstractAuthProvider authProvider;
    private final ObjectMapper objectMapper;
    String AUTH_HEADER = "Authorization";
    UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {


        log.info("Handling Auth");

        String requestUri = urlPathHelper.getRequestUri(request);
        String method = request.getMethod();

        log.info("Request URI - {}", requestUri);

        if(!authProvider.isExcluded(requestUri, method)) {
            String authHeaderValue = request.getHeader(AUTH_HEADER);

            if(StringUtils.isBlank(authHeaderValue)) {
                String errorMessage = "Auth token not provided in header. Please add header "
                        + AUTH_HEADER + " with valid Auth token.";
                addError(errorMessage, request, response);
                return;
            }

            //authenticate
            UserDetail userDetail =
                    authProvider.authenticate(authHeaderValue);

            log.info("user detail - {}", userDetail);

            if(Objects.isNull(userDetail)) {
                String errorMessage = "Authentication failure.";
                addError(errorMessage, request, response);
                return;
            }

            //authorize
            boolean authorized =
                    authProvider.authorize(userDetail, requestUri, method);

            if(!authorized) {
                String errorMessage = "Authorization failure.";
                addError(errorMessage, request, response);
                return;
            }
        }
        else {
            log.info("URI in whitelist. Security checks not applied.");
        }



        filterChain.doFilter(request, response);

        logger.info("Completed Auth Filter");
    }

    private void addError(
            String errorMessage,
            HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/unauthorized");
        body.put("title", "Auth Error");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", errorMessage);
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Invalid-Auth");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }

}
