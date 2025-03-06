package com.homihq.db2rest.config;

import com.homihq.db2rest.interceptor.DatabaseContextRequestInterceptor;
import com.homihq.db2rest.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    CorsConfigProperties properties;



    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(databaseContextRequestInterceptor);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(name = "cors.enabled", havingValue = "true")
    public CorsFilter corsFilter(CorsConfigProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        for (CorsConfigProperties.CorsMapping mapping : properties.getMappings()) {
            CorsConfiguration config = new CorsConfiguration();

            // Process allowed origins (trim, remove duplicates)
            List<String> allowedOrigins = Arrays.stream(mapping.getAllowedOrigin().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedOrigins(allowedOrigins);

            // Process allowed headers (trim, remove duplicates)
            List<String> allowedHeaders = Arrays.stream(mapping.getAllowedHeader().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedHeaders(allowedHeaders);

            // Process allowed methods (trim, remove duplicates)
            List<String> allowedMethods = Arrays.stream(mapping.getAllowedMethod().split(","))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());
            config.setAllowedMethods(allowedMethods);

            System.out.println("CORS Config -> Path: " + mapping.getMapping() +
                    ", Origins: " + allowedOrigins +
                    ", Headers: " + allowedHeaders +
                    ", Methods: " + allowedMethods);

            source.registerCorsConfiguration(mapping.getMapping(), config);
        }

        return new CorsFilter(source);
    }
}
