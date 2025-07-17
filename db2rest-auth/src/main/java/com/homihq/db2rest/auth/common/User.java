package com.homihq.db2rest.auth.common;

import java.util.List;

public record User(String username, String password, List<String> roles) {
}
