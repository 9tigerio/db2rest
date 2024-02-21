package com.homihq.db2rest.auth.to;

import lombok.Builder;

// TODO: Need to make request for extra authorization

public record VerifyKeyRequest(String key) {
}
