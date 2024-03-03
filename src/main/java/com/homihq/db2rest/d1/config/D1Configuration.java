package com.homihq.db2rest.d1.config;

import com.homihq.db2rest.d1.D1Dialect;
import com.homihq.db2rest.d1.D1OperationService;
import com.homihq.db2rest.d1.D1RestClient;
import com.homihq.db2rest.d1.D1SchemaManager;
import com.homihq.db2rest.schema.AliasGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "d1")
@Slf4j
public class D1Configuration {

    private String D1_API_URL_TEMPLATE = "https://api.cloudflare.com/client/v4/accounts/{ACCOUNT_ID}/d1/database/{DB_ID}/query";

    @Value("${d1.accountId}")
    private String accountId;

    @Value("${d1.dbId}")
    private String dbId;

    @Value("${d1.apiKey}")
    private String apiKey;

    @Bean
    public D1Dialect d1Dialect() {
        return new D1Dialect();
    }

    @Bean
    public D1OperationService d1OperationService(RestTemplateBuilder restTemplateBuilder) {
        return new D1OperationService(db1RestClient(restTemplateBuilder));
    }

    @Bean
    public D1RestClient db1RestClient(RestTemplateBuilder restTemplateBuilder) {
        String dbUrl = D1_API_URL_TEMPLATE.replace("{ACCOUNT_ID}", accountId)
                .replace("{DB_ID}", dbId);

        return new D1RestClient(dbUrl, restTemplateBuilder.build(), apiKey);
    }



    @Bean
    public D1SchemaManager schemaManager(RestTemplateBuilder restTemplateBuilder, AliasGenerator aliasGenerator) {
        log.info("D1 schema manager.");
        return new D1SchemaManager(db1RestClient(restTemplateBuilder), aliasGenerator);
    }
}
