package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class AuthenticationFailedException extends ErrorResponseException {

    public AuthenticationFailedException(String message) {
        super(HttpStatus.UNAUTHORIZED, asProblemDetail(message), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, message);
        problemDetail.setTitle("Unable to authenticate");
        problemDetail.setType(URI.create("https://db2rest.com/error/invalid-auth-key"));
        problemDetail.setProperty("errorCategory", "Invalid-AuthKey");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
