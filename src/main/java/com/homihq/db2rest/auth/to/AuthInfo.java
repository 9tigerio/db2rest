package com.homihq.db2rest.auth.to;

public class AuthInfo {
    public String code;

    public boolean enabled;

    public int expires;

    public boolean valid;

    public String keyId;

    public VerifyKeyResponse.Meta meta;

    public String name;

    public String ownerId;

    public String[] permissions;

    public VerifyKeyResponse.RateLimit rateLimit;

    public int remaining;

    static class Meta {
        String[] roles;
        String stripeCustomerId;
    }

    static class RateLimit {
        int limit;
        int remaining;
        int reset;
    }
}
