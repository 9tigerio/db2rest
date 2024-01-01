package com.homihq.db2rest;

import com.homihq.db2rest.mybatis.DB2RTable;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static org.mybatis.dynamic.sql.SqlBuilder.*;
import static org.mybatis.dynamic.sql.select.SelectDSL.select;

@Component
@Slf4j
public class MyBatisDynamic implements CommandLineRunner {

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
                .render(RenderingStrategies.MYBATIS3);

        log.info("SELECT - {}", selectStatement.getSelectStatement());

        log.info("SELECT PARAMS - {}", selectStatement.getParameters());


    }
}
