package com.homihq.db2rest.auth.unkey;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.homihq.db2rest.auth.unkey.service.UnKeyAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@ConditionalOnProperty(name = "db2rest.unKeyDev.enabled", havingValue = "true")
@Slf4j
public class UnKeyAuthConfiguration {

    @Bean
    public UnKeyAuthService unKeyAuthService(RestTemplateBuilder restTemplateBuilder) {
        return new UnKeyAuthService(restTemplateBuilder);
    }

    @Bean
    public UnKeyAuthFilter unKeyAuthFilter(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        return new UnKeyAuthFilter(unKeyAuthService(restTemplateBuilder), objectMapper);
    }

}
