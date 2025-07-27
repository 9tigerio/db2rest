package com.db2rest.jdbc.dialect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(TestConfiguration.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DB2RestDB2Dialect db2RestDB2Dialect(ObjectMapper objectMapper) {
        return new DB2RestDB2Dialect(objectMapper);
    }
}
