package com.homihq.db2rest.auth.common;

import java.util.List;

public record UserDetail(String principal, List<String> roles) {


    public String[] getRoles() {
        return roles.toArray(new String[0]);
    }

}
