package com.homihq.db2rest.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.dynamic.sql.BindableColumn;
import org.mybatis.dynamic.sql.render.SpringNamedParameterRenderingStrategy;

@Slf4j
public class DB2RestRenderingStrategy extends SpringNamedParameterRenderingStrategy {

    @Override
    public String getRecordBasedInsertBinding(BindableColumn<?> column, String prefix, String parameterName) {
        log.info("Here - 1");
        return super.getRecordBasedInsertBinding(column, parameterName);
    }

    @Override
    public String getRecordBasedInsertBinding(BindableColumn<?> column, String parameterName) {
        log.info("Here - 2");
        return super.getRecordBasedInsertBinding(column, parameterName);
    }
}
