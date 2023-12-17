package com.homihq.db2rest.rsql;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class RsqlTestCommandLineRunner implements CommandLineRunner {

    private final FilterBuilderService filterBuilderService;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    @Override
    public void run(String... args) throws Exception {
        /*
        String tableName = "actors";

       String rSql = "first_name!=john;last_name==bachchan";
        String whereClause =
                filterBuilderService.getWhereClause(tableName, rSql);

        log.info("whereClause = {}", whereClause);
        String dbName =
        dataSource.getConnection().getMetaData().getDatabaseProductName();

        log.info("dbName = {}", dbName);

         */

        //testRelation();
    }

    private void testRelation() {

        String sql = """
            SELECT 
                *
            FROM 
                films a 
                ,directors d
            WHERE
                a.director_id = d.id

""";

        List data =
        jdbcTemplate.queryForList(sql);



        log.info("DATA - {}", data);
    }
}
