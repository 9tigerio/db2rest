package com.homihq.db2rest.auth.common;

public abstract class AuthContext {
    private static final ThreadLocal<AuthInfo> currentAuthInfo = new ThreadLocal<AuthInfo>();

    public static void setCurrentAuthInfo(AuthInfo authInfo) {
        currentAuthInfo.set(authInfo);
    }

    public static AuthInfo getCurrentAuthInfo() {
        return currentAuthInfo.get();
    }

    public static void clear() {
        currentAuthInfo.remove();
    }
}
