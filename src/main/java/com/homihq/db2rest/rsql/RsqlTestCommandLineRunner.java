package com.homihq.db2rest.rsql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

//@Component
@RequiredArgsConstructor
@Slf4j
public class RsqlTestCommandLineRunner implements CommandLineRunner {

    private final FilterBuilderService filterBuilderService;
    private final DataSource dataSource;
    @Override
    public void run(String... args) throws Exception {
        String tableName = "actors";

       String rSql = "first_name!=john;last_name==bachchan";
        String whereClause =
                filterBuilderService.getWhereClause(tableName, rSql);

        log.info("whereClause = {}", whereClause);
        String dbName =
        dataSource.getConnection().getMetaData().getDatabaseProductName();

        log.info("dbName = {}", dbName);
    }
}
