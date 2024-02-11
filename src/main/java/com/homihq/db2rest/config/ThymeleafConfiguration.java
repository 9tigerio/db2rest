package com.homihq.db2rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;


@Configuration
public class ThymeleafConfiguration {

    @Bean("textTemplateResolver")
    public SpringResourceTemplateResolver textTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/sql-templates/");
        templateResolver.setSuffix(".sql");
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(3);
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(false);

        return templateResolver;
    }
}
