package com.homihq.db2rest.auth.data;


import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.ResourceRole;
import com.homihq.db2rest.auth.common.User;

import java.util.List;

public class NoAuthdataProvider implements AuthDataProvider {
    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return List.of();
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }
}
