package com.homihq.db2rest.rest.read.helper;

import com.homihq.db2rest.mybatis.MyBatisTable;
import lombok.*;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlCriterion;
import org.mybatis.dynamic.sql.SqlTable;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.mybatis.dynamic.sql.select.SelectDSL.select;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReadContext {

    String schemaName;
    String tableName;
    String select;
    String filter;
    Pageable pageable;
    Sort sort;

    SqlTable from;
    List<MyBatisTable> tables;

    QueryExpressionDSL<SelectModel> queryExpressionDSL;


    public void addWhereClause(SqlCriterion condition) {
        queryExpressionDSL.where(condition);
    }

    private List<BasicColumn> getAllColumns() {
       return tables.stream()
                .flatMap(t -> t.getSqlColumnList().stream())
                .map(t -> (BasicColumn)t)
                .toList();
    }

    public SqlColumn<?> getSortColumn(String columnName) {
        //for now just support root table

        return from.column(columnName);
    }

    public void createSelect() {

        List<BasicColumn> columns = getAllColumns();

        queryExpressionDSL = select(columns).from(from);

    }

    public String prepareSQL() {

        return queryExpressionDSL.build().render(RenderingStrategies.SPRING_NAMED_PARAMETER).getSelectStatement();

    }



}
