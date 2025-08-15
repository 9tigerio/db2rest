package com.homihq.db2rest.auth.exception;

import org.springframework.core.NestedRuntimeException;

public class AuthException extends NestedRuntimeException {
    public AuthException(String msg) {
        super(msg);
    }
}
