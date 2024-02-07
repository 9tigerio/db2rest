package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.FindOneResponse;
import com.homihq.db2rest.rest.read.helper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class FindOneService {

    private final SelectBuilder selectBuilder;
    private final JoinBuilder joinBuilder;
    private final WhereBuilder whereBuilder;
    private final LimitPaginationBuilder limitPaginationBuilder;
    private final SortBuilder sortBuilder;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public FindOneResponse findOne(String tableName, String select, String filter) {

        Pageable currPage = Pageable.ofSize(1).withPage(0);
        ReadContext ctx = ReadContext.builder()
                .pageable(currPage)
                .tableName(tableName).select(select).filter(filter).build();

        selectBuilder.build(ctx);
        joinBuilder.build(ctx);
        whereBuilder.build(ctx);
        limitPaginationBuilder.build(ctx);

        String sql = ctx.prepareSQL();
        Map<String, Object> bindValues = ctx.prepareParameters();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        return new FindOneResponse(
                namedParameterJdbcTemplate.queryForObject(sql, bindValues, Object.class));
    }

}
