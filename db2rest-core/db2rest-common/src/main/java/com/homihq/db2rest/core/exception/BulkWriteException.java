package com.homihq.db2rest.core.exception;

/**
 * Raised when the database rejects a bulk insert batch. The message keeps the
 * wording used by the list based bulk endpoint; the original database error is
 * kept as the cause so callers that must not expose it can log it instead.
 */
public class BulkWriteException extends GenericDataAccessException {

    public BulkWriteException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
