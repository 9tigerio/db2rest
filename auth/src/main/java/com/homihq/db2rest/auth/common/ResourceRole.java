package com.homihq.db2rest.auth.common;

import java.util.List;

public record ResourceRole(String resource, String method, List<String> roles) {
}
