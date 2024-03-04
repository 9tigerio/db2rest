package com.homihq.db2rest.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.jwt.service.JwtAuthService;
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

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthService jwtAuthService;
    private final ObjectMapper objectMapper;
    final String JWT_KEY_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String jwtHeader = request.getHeader(JWT_KEY_HEADER);
        if (StringUtils.isBlank(jwtHeader) || !jwtHeader.startsWith("Bearer ")) {
            addMissingJwtTokenError(request, response);
            return;
        }

        String token = StringUtils.replace(jwtHeader, "Bearer ", "", 1);
        if (!jwtAuthService.isValidToken(token)) {
            addAuthenticationError(request, response);
            return;
        }

        filterChain.doFilter(request, response);
        logger.info("Completed Jwt Auth Filter");
    }


    private void addAuthenticationError(HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/unauthorized");
        body.put("title", "Unauthorized");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "Invalid JWT Token");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Unauthorized");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }

    private void addMissingJwtTokenError(HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/jwt-token-not-found");
        body.put("title", "JWT token not provided in header");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "JWT token not provided in header. Please add header " + JWT_KEY_HEADER + " with valid JWT token.");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Invalid-JWT-Token");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }



}
