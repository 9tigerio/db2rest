package com.homihq.db2rest.rest.read.helper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.SortSpecification;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.select.ColumnSortSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SortBuilder {

    public void build(ReadContext context) {
        Sort sort = context.sort;

        if(!sort.isEmpty()) {


            List<SortSpecification> sortSpecificationList =
            sort.stream().map(s -> getSortSpecification(context,s)).toList();

            context.queryExpressionDSL.orderBy(sortSpecificationList);

        }
    }

    private SortSpecification getSortSpecification(ReadContext context, Sort.Order s) {
        String column = s.getProperty();
        SqlColumn<?> sqlColumn = context.getSortColumn(column);

        return
        s.isAscending() ? new ColumnSortSpecification("", sqlColumn) :
        new ColumnSortSpecification("", sqlColumn).descending();
    }
}
