package com.homihq.db2rest.auth.to;

import lombok.Builder;

@Builder
public class VerifyKeyRequest {
    String apiId;

    String key;
}
