package com.homihq.db2rest.core.exception;

public class SqlTemplateReadException extends RuntimeException {

    public SqlTemplateReadException(String templateName) {
        super("Unable to read template  " + templateName + "." );
    }

}
