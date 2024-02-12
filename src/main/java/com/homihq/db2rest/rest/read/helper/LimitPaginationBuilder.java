package com.homihq.db2rest.rest.read.helper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Deprecated
public class LimitPaginationBuilder {

    public void build(ReadContext context) {
        if(context.pageable.isPaged()) {
            Pageable pageable = context.getPageable();
            context.queryExpressionDSL.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }
    }
}
