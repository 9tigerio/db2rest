package com.homihq.db2rest.auth.service;

import com.homihq.db2rest.auth.to.VerifyKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnkeyDevService {

    private final RestTemplateBuilder restTemplateBuilder;

    String VERIFY_API_KEY_URL = "";



    // TODO: Might need to return user id?
    public boolean verifyApiKey() {
        // TODO: Need to add unkey dev auth creds to connect
        VerifyKeyResponse verifyResponse = restTemplateBuilder.build().getForObject(VERIFY_API_KEY_URL, VerifyKeyResponse.class);
        if(verifyResponse != null) {
            return verifyResponse.valid;
        } else {
            log.error("verify key response from UnkeyDev returned null");
        }

        return false;
    }

}
