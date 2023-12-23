package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class InvalidSchemaException extends ErrorResponseException {


    public InvalidSchemaException(String schemaName) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail("Invalid schema " + schemaName), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Invalid Schema Error");
        problemDetail.setDetail(message);
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/invalid-schema"));
        problemDetail.setProperty("errorCategory", "Invalid-Schema");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
