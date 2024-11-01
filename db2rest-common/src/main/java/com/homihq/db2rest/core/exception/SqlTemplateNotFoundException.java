package com.homihq.db2rest.core.exception;

public class SqlTemplateNotFoundException extends RuntimeException {

    public SqlTemplateNotFoundException(String templateName) {
        super("Template  " + templateName + " not found .");
    }

}
