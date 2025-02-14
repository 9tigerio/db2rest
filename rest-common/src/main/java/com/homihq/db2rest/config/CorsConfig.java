package com.homihq.db2rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "corsconfig")
public class CorsConfig {
    public String allowedCorsOrigin;
    public String allowedCorsHeader;
    public String allowedCorsMethods;

    public String getAllowedCorsOrigin() {
        return allowedCorsOrigin;
    }
    public void setAllowedCorsOrigin(String allowedCorsOrigin) {
        this.allowedCorsOrigin = allowedCorsOrigin;
    }
    public String getAllowedCorsHeader() {
        return allowedCorsHeader;
    }
    public void setAllowedCorsHeader(String allowedCorsHeader) {
        this.allowedCorsHeader = allowedCorsHeader;
    }
    public String getAllowedCorsMethods() {
        return allowedCorsMethods;
    }
    public void setAllowedCorsMethods(String allowedCorsMethods) {
        this.allowedCorsMethods = allowedCorsMethods;
    }   
}
