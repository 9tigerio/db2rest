package com.homihq.db2rest.auth.unkey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.unkey.service.UnKeyAuthService;
import com.homihq.db2rest.auth.unkey.to.UnKeyVerifyResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UnKeyAuthFilter extends OncePerRequestFilter {

    private final UnKeyAuthService unKeyAuthService;
    private final ObjectMapper objectMapper;
    String API_KEY_HEADER = "X-API-KEY";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        logger.info(" *** Apply UnKeyAuth Filter ***");

        // Check the header and return authentication failed if header not present

        String token = request.getHeader(API_KEY_HEADER);
        if (StringUtils.isBlank(token)) {
            addMissingApiKeyError(request, response);
            return;
        }

        Optional<UnKeyVerifyResponse> verifyResponse = unKeyAuthService.verify(token);

        if (verifyResponse.isPresent()) {
            //TODO convert and add to request attributes
            log.info("Unkey response - {}", verifyResponse.get());
            if (!verifyResponse.get().isValidKey()) {
                addAuthenticationError(request, response);
                return;
            }
        } else {
            addAuthenticationError(request, response);
            return;
        }

        filterChain.doFilter(request, response);

        logger.info("Completed UnKey Auth Filter");
    }

    private void addAuthenticationError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/unauthorized");
        body.put("title", "Unauthorized");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "Invalid API Key");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Unauthorized");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }

    private void addMissingApiKeyError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/api-key-not-found");
        body.put("title", "API Key not provided in header");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "API Key not provided in header. Please add header " + API_KEY_HEADER + " with valid API key.");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Invalid-API-Key");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }
}
