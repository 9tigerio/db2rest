package com.homihq.db2rest.jdbc.config;


import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.DeleteCreatorTemplate;
import com.homihq.db2rest.jdbc.sql.UpdateCreatorTemplate;
import com.homihq.db2rest.schema.SchemaManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.sql.DataSource;


@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcSqlCreatorConfiguration {

    @Bean
    public CreateCreatorTemplate createCreatorTemplate(SpringTemplateEngine templateEngine) {
        return new CreateCreatorTemplate(templateEngine);
    }

    @Bean
    public UpdateCreatorTemplate updateCreatorTemplate(SpringTemplateEngine templateEngine, SchemaManager schemaManager) {
        return new UpdateCreatorTemplate(templateEngine, schemaManager);
    }

    @Bean
    public DeleteCreatorTemplate deleteCreatorTemplate(SpringTemplateEngine templateEngine, SchemaManager schemaManager) {
        return new DeleteCreatorTemplate(templateEngine, schemaManager);
    }


}
