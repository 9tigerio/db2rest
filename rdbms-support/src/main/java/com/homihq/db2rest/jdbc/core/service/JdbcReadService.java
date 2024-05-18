package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import com.homihq.db2rest.jdbc.processor.ReadProcessor;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import cz.jirutka.rsql.parser.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class JdbcReadService implements ReadService {
    private final JdbcManager jdbcManager;
    private final DbOperationService dbOperationService;
    private final List<ReadProcessor> processorList;
    private final SqlCreatorTemplate sqlCreatorTemplate;

    @Override
    public Object findAll(ReadContext readContext) {

        try {
            for (ReadProcessor processor : processorList) {
                processor.process(readContext);
            }

            String sql = sqlCreatorTemplate.query(readContext);
            log.info("{}", sql);
            log.info("{}", readContext.getParamMap());
            return dbOperationService.read(
                    jdbcManager.getNamedParameterJdbcTemplate(readContext.getDbName()),
                    readContext.getParamMap(), sql, jdbcManager.getDialect(readContext.getDbName()));
        } catch (DataAccessException e) {
            log.error("Error in read op : " , e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
        catch (Exception e) {
            log.error("Error in read op : " , e);
            throw new GenericDataAccessException("Failed to parse RQL - " + e.getMessage());
        }
    }



}
