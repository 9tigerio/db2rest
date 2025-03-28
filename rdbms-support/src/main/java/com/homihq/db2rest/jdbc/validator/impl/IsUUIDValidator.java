package com.homihq.db2rest.jdbc.validator.impl;


import com.homihq.db2rest.core.exception.PlaceholderConstraintException;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;

import java.util.UUID;

public class IsUUIDValidator implements ConstraintValidator {

    @Override
    public void validate(Object value, String placeholderName) throws PlaceholderConstraintException {
        if (!(value instanceof String) || !isValidUUID((String) value)) {
            throw new PlaceholderConstraintException(placeholderName, "must be a valid UUID.");
        }
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
