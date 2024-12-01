package com.homihq.db2rest.jdbc.validator.impl;

import com.homihq.db2rest.core.exception.PlaceholderConstraintException;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;

public class IsRequiredValidator implements ConstraintValidator {

    @Override
    public void validate(Object object, String placeholderName) throws PlaceholderConstraintException {
        if (object == null || (object instanceof String value && value.trim().isEmpty())) {
            throw new PlaceholderConstraintException(placeholderName, "is required and cannot be null.");
        }
    }
}
