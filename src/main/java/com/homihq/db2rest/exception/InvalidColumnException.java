package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidColumnException extends ErrorResponseException {

    public InvalidColumnException(String tableName, String columnName) {
        super(HttpStatus.NOT_FOUND, asProblemDetail("Missing column " + tableName + "." + columnName), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message);
        problemDetail.setTitle("Missing Column Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/missing-column"));
        problemDetail.setProperty("errorCategory", "Missing-Column");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
