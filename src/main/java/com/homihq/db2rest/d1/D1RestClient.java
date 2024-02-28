package com.homihq.db2rest.d1;

import com.homihq.db2rest.d1.model.D1Column;
import com.homihq.db2rest.d1.model.D1PostRequest;
import com.homihq.db2rest.d1.model.D1PostResponse;
import com.homihq.db2rest.d1.model.D1Table;
import com.homihq.db2rest.exception.GenericDataAccessException;
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

    String SQL_GET_COLUMNS_BY_TABLE = """
            
            SELECT pti.* FROM pragma_table_info(?) pti
            
            """;

    public List<D1Table> getMetaDataAllTables() {

        D1PostResponse response = callD1(SQL_GET_ALL_TABLES, List.of());

        if(response == null || !response.success() || response.result().isEmpty()) {
            throw new GenericDataAccessException("Unable to retrieve table metadata from D1");
        }

        return
        response.result().get(0)
                        .results()
                                .stream()
                                .map(item -> new D1Table(null, (String) item.get("tbl_name")))
                                        .toList();

    }

    public List<D1Column> getMetaDataAllColumns(String tableName) {

        D1PostResponse response = callD1(SQL_GET_COLUMNS_BY_TABLE, List.of(tableName));

        if(response == null || !response.success() || response.result().isEmpty()) {
            throw new GenericDataAccessException("Unable to retrieve table metadata from D1");
        }

        return
                response.result().get(0)
                        .results()
                        .stream()
                        .map(item -> new D1Column(
                                (int)item.get("cid"),
                                (String) item.get("name"),
                                (String) item.get("type"),
                                (int) item.get("notnull") == 1,
                                item.get("dflt_value"),
                                (int) item.get("pk") == 1
                            ))
                        .toList();

    }

    private D1PostResponse callD1(String sql, List<Object> params) {
        D1PostRequest d1PostRequest = new D1PostRequest(sql, params);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey); //accessToken can be the secret key you generate.
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<D1PostRequest> request =
                new HttpEntity<D1PostRequest>(d1PostRequest, headers);

        return restTemplate.postForObject(uri, request, D1PostResponse.class);

    }
}
