package com.homihq.db2rest.exception;

import com.homihq.db2rest.core.exception.*;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DeleteOpNotAllowedException.class)
    ProblemDetail handleDeleteOpNotAllowedException(DeleteOpNotAllowedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Delete Operation Not allowed");
        problemDetail.setType(URI.create("https://github.com/kdhrubo/db2rest/delete-bad-request"));
        problemDetail.setProperty("errorCategory", "Delete-Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }


    @ExceptionHandler(AuthenticationFailedException.class)
    ProblemDetail handleAuthenticationFailedException(AuthenticationFailedException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        problemDetail.setTitle("Failed authentication");
        problemDetail.setType(URI.create("https://db2rest.com/error/invalid-auth-key"));
        problemDetail.setProperty("errorCategory", "Invalid-AuthKey");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({SqlTemplateNotFoundException.class})
    ProblemDetail handleTemplateException(SqlTemplateNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("SQL Template Processing Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/sql-template-error"));
        problemDetail.setProperty("errorCategory", "SQL-Template-Processing-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({SqlTemplateReadException.class})
    ProblemDetail handleTemplateException(SqlTemplateReadException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("SQL Template Processing Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/sql-template-error"));
        problemDetail.setProperty("errorCategory", "SQL-Template-Processing-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({PathVariableNamesMissingException.class})
    ProblemDetail handleTemplateException(PathVariableNamesMissingException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("SQL Template Processing Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/sql-template-error"));
        problemDetail.setProperty("errorCategory", "SQL-Template-Processing-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({PathVariableValuesMissingException.class})
    ProblemDetail handleTemplateException(PathVariableValuesMissingException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("SQL Template Processing Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/sql-template-error"));
        problemDetail.setProperty("errorCategory", "SQL-Template-Processing-error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({PlaceholderConstraintException.class})
    ProblemDetail handleCustomPlaceholderException(PlaceholderConstraintException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Placeholder Constraint Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/placeholder-constraint"));
        problemDetail.setProperty("errorCategory", "Placeholder-Constraint-Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler({UnsupportedConstraintException.class})
    ProblemDetail handleUnsupportedConstraintException(UnsupportedConstraintException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Unsupported Constraint Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/unsupported-constraint"));
        problemDetail.setProperty("errorCategory", "Unsupported-Constraint-Error");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(RpcException.class)
    ProblemDetail handleRpcException(RpcException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid Procedure/Function name or IN parameter mismatch");
        problemDetail.setType(URI.create("https://db2rest.com/error/invalid-subroutine-request"));
        problemDetail.setProperty("errorCategory", "Invalid-SubRoutine-Request");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler(InvalidTableException.class)
    ProblemDetail handleInvalidTableException(InvalidTableException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Missing Table Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/missing-table"));
        problemDetail.setProperty("errorCategory", "Missing-Table");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }


    @ExceptionHandler(InvalidOperatorException.class)
    ProblemDetail handleInvalidOperatorException(InvalidOperatorException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Invalid Operator Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/invalid-operator"));
        problemDetail.setProperty("errorCategory", "Invalid-Operator");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;

    }

    @ExceptionHandler({GenericDataAccessException.class, RuntimeException.class})
    ProblemDetail handleGenericDataAccessException(GenericDataAccessException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problemDetail.setTitle("Generic Data Access Error");
        problemDetail.setType(URI.create("https://db2rest.com/error/generic-error"));
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
        body.put("type", "https://db2rest.com/error/invalid-arguments");
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
