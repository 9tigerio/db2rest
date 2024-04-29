package com.homihq.db2rest.core.exception;



public class InvalidOperatorException extends RuntimeException {


    public InvalidOperatorException(String message, String operator) {
        super(message + operator);
    }

}
