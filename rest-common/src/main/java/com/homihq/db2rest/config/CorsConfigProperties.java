package com.homihq.db2rest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsConfigProperties {
    private String enabled;
    private List<CorsMapping> mappings;

    public String isEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public List<CorsMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CorsMapping> mappings) {
        this.mappings = mappings;
    }

    public static class CorsMapping {
        private String mapping;
        private String allowedOrigin;
        private String allowedHeader;
        private String allowedMethod;

        public String getMapping() {
            return mapping;
        }

        public void setMapping(String mapping) {
            this.mapping = mapping;
        }

        public String getAllowedOrigin() {
            return allowedOrigin;
        }

        public void setAllowedOrigin(String allowedOrigin) {
            this.allowedOrigin = allowedOrigin;
        }

        public String getAllowedHeader() {
            return allowedHeader;
        }

        public void setAllowedHeader(String allowedHeader) {
            this.allowedHeader = allowedHeader;
        }

        public String getAllowedMethod() {
            return allowedMethod;
        }

        public void setAllowedMethod(String allowedMethod) {
            this.allowedMethod = allowedMethod;
        }
    }
}