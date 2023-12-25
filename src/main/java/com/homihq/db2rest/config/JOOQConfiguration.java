package com.homihq.db2rest.config;

import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DefaultConfiguration;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JOOQConfiguration {
    @Bean
    public DefaultConfigurationCustomizer configurationCustomiser() {
        return (DefaultConfiguration c) -> c.settings()
                //.withRenderCatalog(false)
                //.withRenderSchema(false)
                .withRenderFormatted(true)
                .withRenderQuotedNames(RenderQuotedNames.NEVER);
    }
}
