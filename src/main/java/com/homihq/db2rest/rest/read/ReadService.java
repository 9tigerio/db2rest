package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.helper.*;
import com.homihq.db2rest.rest.read.model.QueryRequest;
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
public class ReadService {

    private final SelectBuilder selectBuilder;
    private final JoinBuilder joinBuilder;
    private final WhereBuilder whereBuilder;
    private final LimitPaginationBuilder limitPaginationBuilder;
    private final SortBuilder sortBuilder;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Object findAll(String schemaName, String tableName, String select, String filter,
                                     Pageable pageable, Sort sort) {
        ReadContext ctx = ReadContext.builder()
                .pageable(pageable).sort(sort)
                .schemaName(schemaName)
                .tableName(tableName).select(select).filter(filter).build();

        selectBuilder.build(ctx);
        joinBuilder.build(ctx);
        whereBuilder.build(ctx);
        limitPaginationBuilder.build(ctx);
        sortBuilder.build(ctx);

        String sql = ctx.prepareSQL();
        Map<String,Object> bindValues = ctx.prepareParameters();

        log.info("SQL - {}", sql);
        log.info("Bind variables - {}", bindValues);

        return namedParameterJdbcTemplate.queryForList(sql, bindValues);

    }

    Object findByCustomQuery(QueryRequest queryRequest) {
        return queryRequest.isSingle() ?
                namedParameterJdbcTemplate.queryForMap(queryRequest.getSql(), queryRequest.getParams()) :
                namedParameterJdbcTemplate.queryForList(queryRequest.getSql(), queryRequest.getParams());
    }
}
