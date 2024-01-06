package com.homihq.db2rest.rest.query.helper;

import com.homihq.db2rest.mybatis.MyBatisTable;
import lombok.*;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static org.mybatis.dynamic.sql.select.SelectDSL.select;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryContext {

    String schemaName;
    String tableName;
    String select;
    String filter;
    Pageable pageable;

    SqlTable from;
    List<MyBatisTable> tables;

    QueryExpressionDSL<SelectModel> queryExpressionDSL;

    public void addWhereClause(SqlCriterion condition) {
        queryExpressionDSL.where(condition);
    }

    public void createSelect() {

        List<BasicColumn> columns = tables.stream()
                .flatMap(t -> t.getSqlColumnList().stream())
                .map(t -> (BasicColumn)t)
                .toList();

        queryExpressionDSL = select(columns).from(from);

    }

    public String prepareSQL() {

        return queryExpressionDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER).getSelectStatement();

    }



}
