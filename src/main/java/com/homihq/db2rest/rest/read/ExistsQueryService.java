package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.dbop.JdbcOperationService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.rest.read.dto.ExistsResponse;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.rest.read.processor.ReadProcessor;
import com.homihq.db2rest.rest.read.sql.QueryCreatorTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExistsQueryService {

	private final JdbcOperationService jdbcOperationService;

	private final List<ReadProcessor> processorList;
	private final QueryCreatorTemplate queryCreatorTemplate;

	public ExistsResponse exists(ReadContext readContext) {
		 for (ReadProcessor processor : processorList) {
            processor.process(readContext);
        }

        String sql = queryCreatorTemplate.createExistsQuery(readContext);
        log.info("{}", sql);
        log.info("{}", readContext.getParamMap());

        try {
			return jdbcOperationService.exists(readContext.getParamMap(), sql);

		} catch (DataAccessException e) {
            log.error("Error in exists op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
	}


}
