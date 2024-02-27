package com.homihq.db2rest.d1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class D1RestClient {

    private final String uri;
    private final RestTemplate restTemplate;
    private final String apiKey;

    String SQL_GET_ALL_TABLES = """
            SELECT * FROM sqlite_master AS t WHERE t.type = 'table'
            """;
    public void getMetaData() {

        D1PostRequest d1PostRequest = new D1PostRequest(SQL_GET_ALL_TABLES, List.of());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); //accessToken can be the secret key you generate.
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<D1PostRequest> request =
                new HttpEntity<D1PostRequest>(d1PostRequest, headers);

        Map response = restTemplate.postForObject(uri, request, Map.class);

        log.info("response - {}", response);
    }
}
