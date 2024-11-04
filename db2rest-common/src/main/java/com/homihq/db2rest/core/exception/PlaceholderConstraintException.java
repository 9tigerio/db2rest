package com.homihq.db2rest.core.exception;

public class PlaceholderConstraintException extends RuntimeException  {

	public PlaceholderConstraintException(String namedParam, String msg) {
		super("Placeholder Error: " + namedParam + " " + msg);
	}
}
