package com.homihq.db2rest.jdbc.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;


@Configuration
@ConditionalOnBean(DataSource.class)
public class JdbcSqlCreatorConfiguration {




}
