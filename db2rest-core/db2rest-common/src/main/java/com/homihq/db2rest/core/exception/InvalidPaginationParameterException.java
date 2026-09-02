package com.homihq.db2rest.core.exception;

public class InvalidPaginationParameterException extends RuntimeException {

    public InvalidPaginationParameterException(String parameter, String message) {
        super("Invalid pagination parameter '" + parameter + "': " + message);
    }
}
