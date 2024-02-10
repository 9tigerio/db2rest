package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidOperatorException extends ErrorResponseException {


    public InvalidOperatorException(String message, String operator) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(message + operator), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Invalid Operator Error");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/invalid-operator"));
        problemDetail.setProperty("errorCategory", "Invalid-Operator");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
