package com.homihq.db2rest.auth.common;

import java.util.List;

public record ApiKey(
        String key,
        List<String> roles,
        boolean active
) {
}
