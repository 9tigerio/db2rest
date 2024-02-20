package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidTableException extends ErrorResponseException {


    public InvalidTableException(String tableName) {
        super(HttpStatus.NOT_FOUND, asProblemDetail("Missing table " + tableName), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problemDetail.setTitle("Missing Table Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/missing-table"));
        problemDetail.setProperty("errorCategory", "Missing-Table");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
