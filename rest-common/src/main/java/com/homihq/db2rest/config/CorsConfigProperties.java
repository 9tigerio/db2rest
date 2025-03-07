package com.homihq.db2rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cors", ignoreUnknownFields = true, ignoreInvalidFields = true )
@Data
public class CorsConfigProperties {
    private String enabled;
    private List<CorsMapping> mappings;


    @Data
    public static class CorsMapping {
        private String mapping;
        private String allowedOrigins;
        private String allowedHeaders;
        private String allowedMethods;

    }
}
