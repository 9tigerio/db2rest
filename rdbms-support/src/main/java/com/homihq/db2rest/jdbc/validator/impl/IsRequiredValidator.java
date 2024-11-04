package com.homihq.db2rest.jdbc.validator.impl;

import com.homihq.db2rest.core.exception.PlaceholderConstraintException;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;

public class IsRequiredValidator implements ConstraintValidator {

	@Override
	public void validate(Object value, String placeholderName) throws PlaceholderConstraintException {
		if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
			throw new PlaceholderConstraintException(placeholderName, "is required and cannot be null.");
		}
	}
}
