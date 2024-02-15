package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateContext;
import com.homihq.db2rest.rest.create.param.ParameterValueMapCreator;
import com.homihq.db2rest.rest.create.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkCreateService {

    private final TSIDProcessor tsidProcessor;


    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CreateCreatorTemplate createCreatorTemplate;
    private final ParameterValueMapCreator parameterValueMapCreator;
    private final SchemaManager schemaManager;


    @Transactional
    public Pair<int[], List<Map<String, Object>>> saveBulk(String schemaName, String tableName, List<Map<String, Object>> dataList,
                                                           String tsId, String tsIdType) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) throw new GenericDataAccessException("No data provided");
        try {
            for (Map<String, Object> data : dataList)
                tsidProcessor.processTsId(data, tsId, tsIdType);

            DbTable dbTable = StringUtils.isNotBlank(schemaName) ?
                    schemaManager.getOneTableV2(schemaName, tableName) :
                    schemaManager.getTableV2(tableName);

            List<DbColumn> columnList = dbTable.buildColumns();

            List<String> columns = columnList.stream().map(DbColumn::name).toList();

            List<Map<String, Object>> valueList = new ArrayList<>();

            for (Map<String, Object> data : dataList) {
                valueList.add(parameterValueMapCreator.prepareValues(columnList, data, columns, tsId));
            }

            CreateContext context = new CreateContext(dbTable, columnList, valueList.get(0));

            String sql = createCreatorTemplate.createQuery(context);

            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(valueList.toArray());

            log.debug("SQL -> {}", sql);

            int[] updateCounts;
            KeyHolder keyHolder = new GeneratedKeyHolder();


            updateCounts = namedParameterJdbcTemplate.batchUpdate(sql, batch, keyHolder);

            return Pair.of(updateCounts, keyHolder.getKeyList());
        } catch (DataAccessException e) {
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }


    }


}
