package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidColumnException extends ErrorResponseException {


    public InvalidColumnException(String tableName, String columnName) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail("Invalid column " + tableName + "." + columnName), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Invalid Column Error");
        problemDetail.setDetail(message);
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/invalid-column"));
        problemDetail.setProperty("errorCategory", "Invalid-Column");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
