package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.processor.QueryCreatorTemplate;
import com.homihq.db2rest.rest.read.processor.pre.ReadPreProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReadService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final List<ReadPreProcessor> processorList;
    private final QueryCreatorTemplate queryCreatorTemplate;


    public Object findAll(ReadContextV2 readContextV2) {
        try {
            for (ReadPreProcessor processor : processorList) {
                processor.process(readContextV2);
            }

            String sql = queryCreatorTemplate.createQuery(readContextV2);
            log.info("{}", sql);

            return namedParameterJdbcTemplate.queryForList(sql, readContextV2.getParamMap());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
