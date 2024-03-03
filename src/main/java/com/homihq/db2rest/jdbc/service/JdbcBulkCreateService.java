package com.homihq.db2rest.jdbc.service;

import com.homihq.db2rest.core.DbOperationService;
import com.homihq.db2rest.core.Dialect;
import com.homihq.db2rest.core.service.BulkCreateService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.jdbc.sql.CreateCreatorTemplate;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateContext;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.SchemaCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class JdbcBulkCreateService implements BulkCreateService {

    private final TSIDProcessor tsidProcessor;
    private final CreateCreatorTemplate createCreatorTemplate;
    private final SchemaCache schemaCache;
    private final DbOperationService dbOperationService;
    private final Dialect dialect;

    @Override
    @Transactional
    public CreateBulkResponse saveBulk(String schemaName, String tableName,
                                       List<String> includedColumns,
                                       List<Map<String, Object>> dataList,
                                       boolean tsIdEnabled) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) throw new GenericDataAccessException("No data provided");

        log.info("** Bulk Insert **");

        try {

            //1. get actual table
            DbTable dbTable = schemaCache.getTable(tableName);

            //2. determine the columns to be included in insert statement
            List<String> insertableColumns = isEmpty(includedColumns) ? new ArrayList<>(dataList.get(0).keySet().stream().toList()) :
                    new ArrayList<>(includedColumns);

            //3. check if tsId is enabled and add those values for PK.
            List<Map<String,Object>> tsIds = new ArrayList<>();
            if (tsIdEnabled) {
                log.debug("Handling TSID");
                List<DbColumn> pkColumns = dbTable.buildPkColumns();

                for(DbColumn pkColumn : pkColumns) {
                    log.debug("Adding primary key columns - {}", pkColumn.name());
                    insertableColumns.add(pkColumn.name());
                }

                for (Map<String, Object> data : dataList) {
                    Map<String,Object> tsIdMap =
                    tsidProcessor.processTsId(data, pkColumns);

                    tsIds.add(tsIdMap);
                }
            }

            for(Map<String,Object> data : dataList)
                this.dialect.processTypes(dbTable, insertableColumns, data);

            log.debug("Finally insertable columns - {}", insertableColumns);

            CreateContext context = new CreateContext(dbTable, insertableColumns);
            String sql = createCreatorTemplate.createQuery(context);

            log.info("SQL - {}", sql);
            log.info("Data - {}", dataList);

            CreateBulkResponse createBulkResponse = dbOperationService.batchUpdate(dataList, sql, dbTable);

            if(tsIdEnabled && Objects.isNull(createBulkResponse.keys())){
                return new CreateBulkResponse(createBulkResponse.rows(), tsIds);
            }

            return createBulkResponse;

        } catch (DataAccessException e) {
            log.error("Error", e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }


    }



}
