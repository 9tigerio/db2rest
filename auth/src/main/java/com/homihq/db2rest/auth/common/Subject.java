package com.homihq.db2rest.auth.common;

import java.util.List;

public record Subject (String principal, List<String> roles, String requestedUri) { }
