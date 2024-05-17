package com.homihq.db2rest.auth.data;

import com.homihq.db2rest.auth.common.ApiResource;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.User;

import java.util.List;

public class FileAuthDataProvider implements AuthDataProvider {


    @Override
    public List<ApiResource> getApiResources() {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }
}
