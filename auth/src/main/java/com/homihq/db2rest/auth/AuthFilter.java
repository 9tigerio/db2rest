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
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final List<AbstractAuthProvider> authProviders;
    private final ObjectMapper objectMapper;
    String AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {


        log.info("Handling Auth");
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        String requestUri = urlPathHelper.getRequestUri(request);
        String method = request.getMethod();

        log.info("Request URI - {}", requestUri);

        String authHeaderValue = request.getHeader(AUTH_HEADER);

        if(StringUtils.isBlank(authHeaderValue)) {
            addMissingAuthTokenError(request, response);
            return;
        }

        Optional<AbstractAuthProvider> authProvider = authProviders.stream()
                                            .filter(ap -> ap.canHandle(authHeaderValue))
                                            .findFirst();

        if(authProvider.isEmpty()) {
            addAuthenticationError(request, response);
            return;
        }
        else{

            UserDetail userDetail =
            authProvider.get().authenticate(authHeaderValue);

            //
            authProvider.get().authorize(userDetail, requestUri, method);
        }

        filterChain.doFilter(request, response);

        logger.info("Completed Auth Filter");
    }

    private void addMissingAuthTokenError(HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/jwt-token-not-found");
        body.put("title", "Auth token not provided in header");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "Auth token not provided in header. Please add header " + AUTH_HEADER + " with valid Auth token.");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Invalid-Auth-Token");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }


    private void addAuthenticationError(HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/unauthorized");
        body.put("title", "Unauthorized");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "No Auth Handler found");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Auth-error");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }



}
