package com.homihq.db2rest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class CorsFilterConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(name = "cors.enabled", havingValue = "true")
    public CorsFilter corsFilter(CorsConfigProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        for (CorsConfigProperties.CorsMapping mapping : properties.getMappings()) {
            CorsConfiguration config = new CorsConfiguration();

            // Process allowed origins (trim, remove duplicates)
            List<String> allowedOrigins = Arrays.stream(mapping.getAllowedOrigins().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedOrigins(allowedOrigins);

            // Process allowed headers (trim, remove duplicates)
            List<String> allowedHeaders = Arrays.stream(mapping.getAllowedHeaders().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedHeaders(allowedHeaders);

            // Process allowed methods (trim, remove duplicates)
            List<String> allowedMethods = Arrays.stream(mapping.getAllowedMethods().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedMethods(allowedMethods);

            log.info("CORS Config -> Path: {}, Origins: {} , Headers: {}, Methods: {}" ,
                    mapping.getMapping() , allowedOrigins, allowedHeaders ,  allowedMethods);

            source.registerCorsConfiguration(mapping.getMapping(), config);
        }

        return new CorsFilter(source);
    }

}
