package com.homihq.db2rest.core.exception;

/**
 * Raised when the database rejects the statement rendered from a SQL template. The
 * message does not include the database error; that is logged on the server. For
 * insert, update and delete templates the transaction has been rolled back.
 */
public class SqlTemplateExecutionException extends GenericDataAccessException {

    public SqlTemplateExecutionException(String templateName, boolean rolledBack) {
        super("Template " + templateName + " failed: the database rejected the statement."
                + (rolledBack ? " The transaction was rolled back." : ""));
    }
}
