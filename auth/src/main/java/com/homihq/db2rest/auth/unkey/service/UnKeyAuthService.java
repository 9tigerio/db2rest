package com.homihq.db2rest.auth.unkey.service;

import com.homihq.db2rest.auth.unkey.to.UnKeyVerifyRequest;
import com.homihq.db2rest.auth.unkey.to.UnKeyVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class UnKeyAuthService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${db2rest.unKeyDev.url}")
    private String unKeyUrl;

    @Value("${db2rest.unKeyDev.rootKey}")
    private String unKeyRootKey;

    public Optional<UnKeyVerifyResponse> verify(String apiKey) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(unKeyRootKey);

        RestTemplate restTemplate = restTemplateBuilder.build();

        UnKeyVerifyRequest unKeyVerifyRequest = new UnKeyVerifyRequest(apiKey);

        var request = new HttpEntity<>(unKeyVerifyRequest, headers);

        try {
            UnKeyVerifyResponse response = restTemplate
                    .postForObject(unKeyUrl, request, UnKeyVerifyResponse.class);

            return Optional.ofNullable(response);

        } catch (RestClientException restClientException) {
            log.error("Error with UnKey verification ", restClientException);
            return Optional.empty();
        }

    }

}
