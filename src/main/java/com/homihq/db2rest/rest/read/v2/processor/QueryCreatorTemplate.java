package com.homihq.db2rest.rest.read.v2.processor;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.mybatis.dynamic.sql.select.SelectDSL.select;

@Component
@Slf4j
public class QueryCreatorTemplate {

    public void createQuery(ReadContextV2 readContextV2) {
        QueryExpressionDSL<SelectModel> queryExpressionDSL = createProjection(readContextV2);

        if(Objects.nonNull(readContextV2.getWhereCondition())) {
            queryExpressionDSL.where(readContextV2.getWhereCondition());
        }

        SelectStatementProvider selectStatementProvider =  queryExpressionDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER);

        log.info("SQL - {}", selectStatementProvider.getSelectStatement());
        log.info("Bind Variables - {}", selectStatementProvider.getParameters());

    }

    protected QueryExpressionDSL<SelectModel> createProjection(ReadContextV2 readContextV2) {
       return
                select(readContextV2.getColumns())
                        .from(readContextV2.getRootTable(), readContextV2.getRootTable().getAlias());
    }
}
