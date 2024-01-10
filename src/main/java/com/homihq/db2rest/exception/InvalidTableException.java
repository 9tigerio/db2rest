package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidTableException extends ErrorResponseException {


    public InvalidTableException(String tableName) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail("Invalid table " + tableName), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Invalid Table Error");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/invalid-table"));
        problemDetail.setProperty("errorCategory", "Invalid-Table");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
