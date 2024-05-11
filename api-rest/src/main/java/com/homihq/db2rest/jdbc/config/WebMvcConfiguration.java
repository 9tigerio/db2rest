package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.jdbc.rest.interceptor.DatabaseContextRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {


    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(databaseContextRequestInterceptor);
    }
}
