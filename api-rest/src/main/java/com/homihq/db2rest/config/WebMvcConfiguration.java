package com.homihq.db2rest.config;

import com.homihq.db2rest.interceptor.DatabaseContextRequestInterceptor;
import com.homihq.db2rest.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    CorsConfig props;


    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(databaseContextRequestInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(props.allowedCorsOrigin.split(",")).map(String::trim).toArray(String[]::new);
        String[] headers = Arrays.stream(props.allowedCorsHeader.split(",")).map(String::trim).toArray(String[]::new);
        String[] methods = Arrays.stream(props.allowedCorsMethods.split(",")).map(String::trim).toArray(String[]::new);

        System.out.println( "Allowed Headers : " + props.allowedCorsHeader + " Allowed Methods : " +props.allowedCorsMethods + " Allowed Origins : " +props.allowedCorsOrigin);

        registry.addMapping("/**").allowedMethods(methods).allowedHeaders(headers).allowedOrigins(origins);
    }
}
