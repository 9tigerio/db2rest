package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class MissingAuthenticationException extends ErrorResponseException {
    public MissingAuthenticationException(String message) {
        super(HttpStatus.UNAUTHORIZED, asProblemDetail(message), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
        problemDetail.setTitle("Missing authentication headers");
        problemDetail.setType(URI.create("https://db2rest.com/error/missing-auth-headers"));
        problemDetail.setProperty("errorCategory", "Missing-Auth-Headers");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }
}
