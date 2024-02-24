package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.rest.read.processor.ReadProcessor;
import com.homihq.db2rest.rest.read.sql.QueryCreatorTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
public class FindOneService {

    private final QueryCreatorTemplate queryCreatorTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final List<ReadProcessor> processorList;

    public Map<String,Object> findOne(ReadContext readContext) {

        for (ReadProcessor processor : processorList) {
            processor.process(readContext);
        }

        String sql = queryCreatorTemplate.createFindOneQuery(readContext);
        Map<String, Object> bindValues = readContext.getParamMap();

        log.debug("SQL - {}", sql);
        log.debug("Params - {}", bindValues);

        try {
            return
            namedParameterJdbcTemplate.queryForMap(sql, bindValues);
        }
        catch (DataAccessException e) {
            log.error("Error in read op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }

}
