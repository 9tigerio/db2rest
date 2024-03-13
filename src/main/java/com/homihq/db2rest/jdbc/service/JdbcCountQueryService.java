package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.service.CountQueryService;
import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.exception.GenericDataAccessException;

import com.homihq.db2rest.rest.read.dto.CountResponse;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.sql.QueryCreatorTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JdbcCountQueryService implements CountQueryService {

    private final DbOperationService dbOperationService;
    private final List<ReadProcessor> processorList;
    private final QueryCreatorTemplate queryCreatorTemplate;

    @Override
    public CountResponse count(ReadContext readContext) {
        for (ReadProcessor processor : processorList) {
            processor.process(readContext);
        }

        String sql = queryCreatorTemplate.createCountQuery(readContext);
        log.info("{}", sql);
        log.info("{}", readContext.getParamMap());

        try {
            return dbOperationService.count(readContext.getParamMap(), sql);
        } catch (DataAccessException e) {
            log.error("Error in read op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }

    }




}
