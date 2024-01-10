package com.homihq.db2rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

public class RpcException extends ErrorResponseException {

    public RpcException(String subRoutineName, Map<String, Object> inParams) {
        super(HttpStatus.BAD_REQUEST,
                asProblemDetail("Procedure/Function name: "
                        + subRoutineName + ", IN parameters: "
                        + inParams.entrySet()), null);
    }

    private static ProblemDetail asProblemDetail(String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
        problemDetail.setTitle("Invalid Procedure/Function name or IN parameter mismatch");
        problemDetail.setDetail(message);
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/invalid-subroutine-request"));
        problemDetail.setProperty("errorCategory", "Invalid-SubRoutine-Request");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
}
