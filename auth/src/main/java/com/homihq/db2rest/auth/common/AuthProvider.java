package com.homihq.db2rest.auth.common;

public interface AuthProvider {

    boolean canHandle(String authHeader);

    Subject handle(String authHeader);
}
