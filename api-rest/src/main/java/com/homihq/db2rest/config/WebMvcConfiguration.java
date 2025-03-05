package com.homihq.db2rest.config;

import com.homihq.db2rest.interceptor.DatabaseContextRequestInterceptor;
import com.homihq.db2rest.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
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
    CorsConfig props;


    private final DatabaseContextRequestInterceptor databaseContextRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(databaseContextRequestInterceptor);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter()  {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(props.allowedCorsOrigin.split(",")));
        config.setAllowedHeaders(Arrays.asList(props.allowedCorsHeader.split(",")));
        config.setAllowedMethods(Arrays.asList(props.allowedCorsMethods.split(",")));


        System.out.println( "Allowed Headers : " + props.allowedCorsHeader + " Allowed Methods : " +props.allowedCorsMethods + " Allowed Origins : " +props.allowedCorsOrigin);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
