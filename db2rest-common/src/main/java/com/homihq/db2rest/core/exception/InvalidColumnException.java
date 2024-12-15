package com.homihq.db2rest.core.exception;

public class InvalidColumnException extends RuntimeException {

    public InvalidColumnException(String tableName, String columnName) {
        super("Column not found " + tableName + "." + columnName);
    }

}
