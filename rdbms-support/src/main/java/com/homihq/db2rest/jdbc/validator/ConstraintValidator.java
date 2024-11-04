package com.homihq.db2rest.jdbc.validator;

import com.homihq.db2rest.core.exception.PlaceholderConstraintException;

public interface ConstraintValidator {
	void validate(Object value, String placeholderName) throws PlaceholderConstraintException;
}
