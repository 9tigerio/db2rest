package com.homihq.db2rest.auth.basic;

import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.AuthProvider;
import com.homihq.db2rest.auth.common.Subject;
import com.homihq.db2rest.auth.common.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@RequiredArgsConstructor
@Slf4j
public class BasicAuthProvider implements AuthProvider {

    private final AuthDataProvider authDataProvider;
    @Override
    public boolean canHandle(String authHeader) {
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Basic ");
    }

    @Override
    public Subject handle(String authHeader) {
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);

        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        User user = authDataProvider.validate(username, password);

        return new Subject(username, user.roles(), "");
    }

}
