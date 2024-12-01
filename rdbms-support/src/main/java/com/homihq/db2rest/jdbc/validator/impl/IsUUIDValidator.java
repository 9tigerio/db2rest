package com.homihq.db2rest.jdbc.validator.impl;


import com.homihq.db2rest.core.exception.PlaceholderConstraintException;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;

import java.util.UUID;

public class IsUUIDValidator implements ConstraintValidator {

    @Override
    public void validate(Object object, String placeholderName) throws PlaceholderConstraintException {
        if (!(object instanceof String value) || !isValidUUID(value)) {
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
