package com.homihq.db2rest.rest.read.v2.processor.post;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.SelectModel;

public interface ReadPostProcessor {

    void process(QueryExpressionDSL<SelectModel> queryExpressionDSL, ReadContextV2 readContextV2);
}
