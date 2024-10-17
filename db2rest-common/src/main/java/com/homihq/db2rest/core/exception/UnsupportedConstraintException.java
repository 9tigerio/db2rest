package com.homihq.db2rest.core.exception;

public class UnsupportedConstraintException extends RuntimeException {
	public UnsupportedConstraintException(String constraint) {
		super("Constraint [" + constraint + "] is not supported");
	};
}
