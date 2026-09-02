package com.homihq.db2rest.core.util;

import com.homihq.db2rest.core.exception.InvalidPaginationParameterException;

public final class PaginationValidator {

    private PaginationValidator() {
    }

    /**
     * Validates read API pagination query parameters.
     * <p>
     * {@code limit}: {@code -1} applies the configured default fetch limit; any positive value is used as-is.
     * {@code offset}: {@code -1} means no offset; any non-negative value is used as-is.
     */
    public static void validate(int limit, long offset) {
        if (limit < -1 || limit == 0) {
            throw new InvalidPaginationParameterException("limit",
                    "must be -1 (use default fetch limit) or a positive integer.");
        }
        if (offset < -1) {
            throw new InvalidPaginationParameterException("offset",
                    "must be -1 (no offset) or a non-negative integer.");
        }
    }
}
