package com.homihq.db2rest.auth.common;

public abstract class AbstractAuthProvider{

    public abstract boolean canHandle(String authHeader);

    public abstract UserDetail authenticate(String authHeader);

    public abstract boolean authorize(UserDetail userDetail, String resourceUri, String method);
}
