package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateContext;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkCreateService {

    private final TSIDProcessor tsidProcessor;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final CreateCreatorTemplate createCreatorTemplate;
    private final SchemaManager schemaManager;


    @Transactional
    public Pair<int[], List<Map<String, Object>>> saveBulk(String schemaName, String tableName,
                                                           List<String> includedColumns,
                                                           List<Map<String, Object>> dataList,
                                                           boolean tsIdEnabled) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) throw new GenericDataAccessException("No data provided");

        try {

            //1. get actual table
            DbTable dbTable = StringUtils.isNotBlank(schemaName) ?
                    schemaManager.getOneTableV2(schemaName, tableName) : schemaManager.getTableV2(tableName);

            //2. determine the columns to be included in insert statement
            List<String> insertableColumns = isEmpty(includedColumns) ? dataList.get(0).keySet().stream().toList() :
                    includedColumns;

            //3. check if tsId is enabled and add those values for PK.
            if (tsIdEnabled) {
                List<DbColumn> pkColumns = dbTable.buildPkColumns();

                for (Map<String, Object> data : dataList) {
                    tsidProcessor.processTsId(data, pkColumns);
                }
            }

            CreateContext context = new CreateContext(dbTable, insertableColumns);
            String sql = createCreatorTemplate.createQuery(context);

            log.info("SQL - {}", sql);

            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(dataList.toArray());

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
