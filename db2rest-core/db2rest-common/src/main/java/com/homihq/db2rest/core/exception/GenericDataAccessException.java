package com.homihq.db2rest.core.exception;

public class GenericDataAccessException extends RuntimeException {

    public GenericDataAccessException(String msg) {
        super(msg);
    }

    public GenericDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
