package com.homihq.db2rest.auth.service;

import com.homihq.db2rest.auth.to.VerifyKeyRequest;
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


    String UNKEY_DEV_DOMAIN = "https://api.unkey.dev/v1";
    String VERIFY_API_KEY_URL = "keys.verifyKey";



    // TODO: Might need to return user id?
    public boolean verifyApiKey(String apiKey) {

        RestTemplate restTemplate = restTemplateBuilder
                .defaultHeader("Content-Type", "application/json")
                .build();

        // TODO: Need how to get apiId
        VerifyKeyRequest request = VerifyKeyRequest.builder()
                    .key(apiKey).apiId("").build();

        VerifyKeyResponse response = restTemplate.postForObject(UNKEY_DEV_DOMAIN + "/" + VERIFY_API_KEY_URL,
                    request, VerifyKeyResponse.class);

        if(response != null) {
            return response.valid;
        } else {
            log.error("verify key response from UnkeyDev returned null");
        }

        return false;
    }

}
