package com.homihq.db2rest.auth.service;

import com.homihq.db2rest.auth.to.VerifyKeyRequest;
import com.homihq.db2rest.auth.to.VerifyKeyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnkeyDevService {

    private final RestTemplateBuilder restTemplateBuilder;


    @Value("${db2rest.unkeyDev.url}")
    String UNKEY_DEV_DOMAIN;
    String VERIFY_API_KEY_URL = "keys.verifyKey";



    public VerifyKeyResponse verifyApiKey(String apiKey) {

        RestTemplate restTemplate = restTemplateBuilder
                .defaultHeader("Content-Type", "application/json")
                .build();

        VerifyKeyRequest request = new VerifyKeyRequest(apiKey);

        VerifyKeyResponse response;
        try {
            response = restTemplate.postForObject(UNKEY_DEV_DOMAIN + "/" + VERIFY_API_KEY_URL,
                    request, VerifyKeyResponse.class);
            if(response != null) {
                return response;
            }
        } catch (RestClientException restClientException) {
            log.error("error connecting to unkey.dev - " + restClientException);
            return null;
        }

        log.error("received null response from unkey.dev");
        return null;
    }

}
