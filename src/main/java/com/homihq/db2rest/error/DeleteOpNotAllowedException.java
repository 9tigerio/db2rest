package com.homihq.db2rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class DeleteOpNotAllowedException extends ErrorResponseException {


    public DeleteOpNotAllowedException(boolean unsafe) {
        super(HttpStatus.BAD_REQUEST, asProblemDetail("Invalid delete operation , unsafe set to " + unsafe), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Delete Operation Not allowed");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/delete-bad-request"));
        problemDetail.setProperty("errorCategory", "Delete-Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
