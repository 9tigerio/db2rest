package com.homihq.db2rest.core.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
public class GlobalRESTExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GenericDataAccessException.class)
    ProblemDetail handleGenericDataAccessException(GenericDataAccessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Generic Data Access Error");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/generic-error"));
        problemDetail.setProperty("errorCategory", "Data-access-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler(InvalidColumnException.class)
    ProblemDetail handleInvalidColumnException(InvalidColumnException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Missing Column Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/missing-column"));
        problemDetail.setProperty("errorCategory", "Missing-Column");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        var body = new LinkedHashMap<>();
        body.put("type", "https://github.com/kdhrubo/db2rest/invalid-arguments");
        body.put("title", "Invalid arguments in the request");
        body.put("status", status.value());
        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Map.of(error.getField(), Objects.requireNonNull(error.getDefaultMessage())))
                .toList();
        body.put("detail", errors);
        body.put("instance", ((ServletWebRequest) request).getRequest().getRequestURI());
        body.put("errorCategory", "Invalid-Arguments");
        body.put("timestamp", Instant.now());
        return new ResponseEntity<>(body, headers, status);
    }



}
