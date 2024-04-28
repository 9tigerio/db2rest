package com.homihq.db2rest.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class GenericDataAccessException extends ErrorResponseException {


    public GenericDataAccessException(String message) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail(message), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Generic Data Access Error");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/generic-error"));
        problemDetail.setProperty("errorCategory", "Data-access-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
