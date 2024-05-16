package com.homihq.db2rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.common.AuthProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final List<AuthProvider> authProviders;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {


        log.info("Handling Auth");

        String AUTH_HEADER = "Authorization";
        String authHeaderValue = request.getHeader(AUTH_HEADER);

        Optional<AuthProvider> authProvider =
        authProviders.stream()
                        .filter(ap -> ap.canHandle(authHeaderValue))
                                .findFirst();

        if(authProvider.isEmpty()) {
            addAuthenticationError(request, response);
            return;
        }
        else{
            authProvider.get().handle(authHeaderValue);
        }

        filterChain.doFilter(request, response);

        logger.info("Completed Auth Filter");
    }


    private void addAuthenticationError(HttpServletRequest request , HttpServletResponse response) throws IOException{
        var body = new LinkedHashMap<>();
        body.put("type", "https://db2rest/unauthorized");
        body.put("title", "Unauthorized");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);

        body.put("detail", "No Auth Handler found");
        body.put("instance", request.getRequestURI());
        body.put("errorCategory", "Unauthorized");
        body.put("timestamp", Instant.now());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        objectMapper.writeValue(response.getWriter(), body);
    }



}
