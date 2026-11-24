package com.homihq.db2rest.core.exception;

/**
 * Raised when a SQL template, or the request that renders it, breaks a template rule:
 * a size or count limit, a malformed placeholder, or a statement type that cannot be
 * determined. Nothing is sent to the database when this is thrown.
 */
public class SqlTemplateValidationException extends GenericDataAccessException {

    public SqlTemplateValidationException(String templateName, String reason) {
        super("Template " + templateName + " rejected: " + reason);
    }
}
