package com.homihq.db2rest.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyVerifierService {

    private final UnkeyDevService unkeyDevService;

    public boolean verifyApiKey(String token) {
        // TODO: Check if need to return user or role
        return unkeyDevService.verifyApiKey(token);
    }

}
