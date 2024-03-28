package com.homihq.db2rest.jdbc.config;


import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.sql.DataSource;


@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcSqlCreatorConfiguration {


    @Bean
    public UpdateCreatorTemplate updateCreatorTemplate(SpringTemplateEngine templateEngine, Dialect dialect) {
        return new UpdateCreatorTemplate(templateEngine, dialect);
    }



}
