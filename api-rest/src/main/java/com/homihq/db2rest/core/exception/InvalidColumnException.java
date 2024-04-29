package com.homihq.db2rest.core.exception;

import org.springframework.core.NestedRuntimeException;


public class InvalidColumnException extends NestedRuntimeException {

    public InvalidColumnException(String tableName, String columnName) {
        super("Missing column " + tableName + "." + columnName, null);
    }

}
