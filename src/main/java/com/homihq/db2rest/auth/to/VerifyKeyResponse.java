package com.homihq.db2rest.auth.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyKeyResponse {

    public String code;

    public boolean enabled;

    public boolean valid;

    public String keyId;

    public Meta meta;


    static class Meta {
        String[] roles;

    }
}
