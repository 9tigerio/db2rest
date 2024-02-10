package com.homihq.db2rest.rest.read.processor.post;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(1)
@RequiredArgsConstructor
public class OrderByPostProcessor implements ReadPostProcessor{
    @Override
    public void process(QueryExpressionDSL<SelectModel> queryExpressionDSL, ReadContextV2 readContextV2) {

        if(readContextV2.getOffset() > -1) {
            queryExpressionDSL.offset(readContextV2.getOffset());
        }

        if(readContextV2.getLimit() > -1) {
            queryExpressionDSL.limit(readContextV2.getLimit());
        }
    }
}
