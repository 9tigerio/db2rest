package com.homihq.db2rest.core.exception;

import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

import java.net.URI;
import java.time.Instant;

public class GenericDataAccessException extends NestedRuntimeException {

    public GenericDataAccessException(String msg) {
        super(msg);
    }
}
