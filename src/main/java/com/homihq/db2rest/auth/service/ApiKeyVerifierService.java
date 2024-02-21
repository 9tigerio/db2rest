package com.homihq.db2rest.auth.service;

import com.homihq.db2rest.auth.to.AuthInfo;
import com.homihq.db2rest.auth.to.VerifyKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyVerifierService {

    private final UnkeyDevService unkeyDevService;

    public AuthInfo getAuthInfo(String token) {
        VerifyKeyResponse response = unkeyDevService.verifyApiKey(token);
        AuthInfo authInfo = new AuthInfo();
        BeanUtils.copyProperties(response, authInfo);
        return authInfo;
    }

}
