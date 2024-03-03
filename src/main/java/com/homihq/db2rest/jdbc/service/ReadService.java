package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.dbop.DbOperationService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.rest.read.sql.QueryCreatorTemplate;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReadService {

    private final DbOperationService dbOperationService;
    private final List<ReadProcessor> processorList;
    private final QueryCreatorTemplate queryCreatorTemplate;

    public Object findAll(ReadContext readContext) {
        for (ReadProcessor processor : processorList) {
            processor.process(readContext);
        }

        String sql = queryCreatorTemplate.createQuery(readContext);
        log.debug("{}", sql);
        log.debug("{}", readContext.getParamMap());

        try {
            return dbOperationService.read(readContext.getParamMap(), sql);
        } catch (DataAccessException e) {
            log.error("Error in read op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }



}
