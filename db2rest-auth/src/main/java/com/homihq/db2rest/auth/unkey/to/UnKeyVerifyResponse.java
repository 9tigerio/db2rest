package com.homihq.db2rest.auth.unkey.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UnKeyVerifyResponse(
        String code,
        boolean enabled,
        int expires,
        boolean valid,
        String keyId,
        String[] permissions
) {

    public boolean isValidKey() {
        return enabled && valid;
    }
}
