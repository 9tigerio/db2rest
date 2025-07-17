package com.homihq.db2rest.core.exception;

public class PathVariableNamesMissingException extends RuntimeException {

    public PathVariableNamesMissingException() {
        super(
                "Path variables names are missing. They must be added as header - 'paths=id,name' ."
        );
    }
}
