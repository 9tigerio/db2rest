package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.rest.read.dto.ExistsResponse;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;


import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JdbcExistsQueryService implements ExistsQueryService {

	private final DbOperationService dbOperationService;
	private final List<ReadProcessor> processorList;
	private final SqlCreatorTemplate sqlCreatorTemplate;

	@Override
    public ExistsResponse exists(ReadContext readContext) {
		 for (ReadProcessor processor : processorList) {
            processor.process(readContext);
        }

        String sql = sqlCreatorTemplate.exists(readContext);
        log.debug("{}", sql);
        log.debug("{}", readContext.getParamMap());

        try {
			return dbOperationService.exists(readContext.getParamMap(), sql);

		} catch (DataAccessException e) {
            log.error("Error in exists op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
	}


}
