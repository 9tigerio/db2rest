package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.jdbc.rest.interceptor.DatabaseContextRequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {


    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("==== Registering DatabaseContextRequestInterceptor ====");
        registry.addInterceptor(databaseContextRequestInterceptor);
    }
}
