package com.homihq.db2rest.auth.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VerifyKeyResponse() {

    public static String code;

    public static boolean enabled;

    public static int expires;

    public static boolean valid;

    public static String keyId;

    public static Meta meta;

    public static String name;

    public static String ownerId;

    public static String[] permissions;

    public static RateLimit rateLimit;

    public static int remaining;

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
