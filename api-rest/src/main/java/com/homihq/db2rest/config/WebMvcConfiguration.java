package com.homihq.db2rest.config;

import com.homihq.db2rest.interceptor.DatabaseContextRequestInterceptor;
import com.homihq.db2rest.config.Db2RestConfigProperties;
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
    Db2RestConfigProperties props;


    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(databaseContextRequestInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(props.getAllowedCorsList().split(","))
                         .map(String::trim)
                         .toArray(String[]::new);
        for(String origin : origins){
            System.out.println("*******************************************" + origin.toString() + "*****************************************************");

        }
        registry.addMapping("/**").allowedMethods("GET", "POST").allowedHeaders("*").allowedOrigins(origins);
    }
}
