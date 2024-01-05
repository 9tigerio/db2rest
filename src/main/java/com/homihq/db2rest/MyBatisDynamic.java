package com.homihq.db2rest;

import com.homihq.db2rest.rsql.operators.CustomRSQLOperators;
import com.homihq.db2rest.rsql.v2.parser.MyBatisFilterVisitor;
import com.homihq.db2rest.schema.SchemaManager;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.select.SelectDSL.select;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyBatisDynamic implements CommandLineRunner {
    private final SchemaManager schemaManager;
    @Override
    public void run(String... args) throws Exception {

        SqlTable language = SqlTable.of("language");
        SqlTable film = SqlTable.of("film");
        SelectStatementProvider selectStatement = select(
                film.column("id"), film.column("title"),
                film.column("year_of_release"), film.column("duration"))
                .from(film, "f")
                .join(language, "lang").on(language.column("language_id"), equalTo(film.column("language_id")))
                .where(film.column("id"), isEqualTo(1))
                .or(film.column("title"), isNotNull())
                .build()
                .render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.info("SELECT - {}", selectStatement.getSelectStatement());

        log.info("SELECT PARAMS - {}", selectStatement.getParameters());

        String filter = "year_of_release==2024";

        Node rootNode = new RSQLParser(CustomRSQLOperators.customOperators()).parse(filter);

        SqlCriterion condition = rootNode.accept(new MyBatisFilterVisitor(film));


        var selectStatement2 = select(
                film.column("id"), film.column("title"),
                film.column("year_of_release"), film.column("duration"))
                .from(film, "f")
                .join(language, "lang").on(language.column("language_id"), equalTo(film.column("language_id")));

        var a = selectStatement2.where(condition).build()
                .render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.info("condition - {}", condition);
        log.info("sql - {}", a.getSelectStatement());
        log.info("sql - {}", a.getParameters());
    }
}
