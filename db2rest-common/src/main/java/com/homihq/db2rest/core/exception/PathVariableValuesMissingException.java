package com.homihq.db2rest.core.exception;

public class PathVariableValuesMissingException extends RuntimeException {
	public PathVariableValuesMissingException() {
		super("Mismatch between number of path keys and user path values");
	}

	public PathVariableValuesMissingException(String key) {
		super("Path variable value is missing for: " + key);
	}
}
