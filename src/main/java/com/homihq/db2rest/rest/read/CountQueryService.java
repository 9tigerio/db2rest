package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.helper.*;

import com.homihq.db2rest.rest.read.dto.CountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class CountQueryService {

    private final SelectBuilder selectBuilder;
    private final WhereBuilder whereBuilder;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public CountResponse count(String tableName, String filter) {
        ReadContext ctx = ReadContext.builder()
                .select("count(*)")
                .tableName(tableName)
                .filter(filter)
                .build();
        selectBuilder.build(ctx);
        whereBuilder.build(ctx);

        String sql = ctx.prepareSQL();
        Map<String, Object> bindValues = ctx.prepareParameters();

        log.info("SQL count - {}", sql);
        log.info("Bind variables - {}", bindValues);

        Long itemCount = namedParameterJdbcTemplate.queryForObject(sql, bindValues, Long.class);

        return new CountResponse(itemCount);
    }


}
