package com.homihq.db2rest.d1;

import com.homihq.db2rest.d1.model.D1PostRequest;
import com.homihq.db2rest.d1.model.D1PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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

        D1PostResponse response = restTemplate.postForObject(uri, request, D1PostResponse.class);

        log.info("response - {}", response);
    }
}
