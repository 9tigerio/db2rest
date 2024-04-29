package com.homihq.db2rest.core.exception;

public class InvalidTableException extends RuntimeException {


    public InvalidTableException(String tableName) {
        super("Missing table " + tableName);
    }


}
