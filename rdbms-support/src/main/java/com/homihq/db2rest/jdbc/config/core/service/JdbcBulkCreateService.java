package com.homihq.db2rest.jdbc.config.core.service;

import com.homihq.db2rest.jdbc.config.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.config.core.DbOperationService;
import com.homihq.db2rest.jdbc.config.dto.CreateContext;
import com.homihq.db2rest.jdbc.config.dto.InsertableColumn;
import com.homihq.db2rest.jdbc.config.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.config.tsid.TSIDProcessor;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
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
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final JdbcSchemaCache jdbcSchemaCache;
    private final DbOperationService dbOperationService;


    @Override
    @Transactional
    public CreateBulkResponse saveBulk(String schemaName, String tableName,
                                       List<String> includedColumns,
                                       List<Map<String, Object>> dataList,
                                       boolean tsIdEnabled, List<String> sequences) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) throw new GenericDataAccessException("No data provided");

        log.debug("** Bulk Insert **");

        try {

            //1. get actual table
            DbTable dbTable = jdbcSchemaCache.getTable(schemaName, tableName);

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

            //4. convert to insertable column object
            List<InsertableColumn> insertableColumnList = new ArrayList<>();

            for(String colName : insertableColumns) {
                insertableColumnList.add(new InsertableColumn(colName, null));
            }

            log.info("Sequences - {}", sequences);
            if(Objects.nonNull(sequences)) { //handle oracle sequence
                for(String sequence : sequences) {
                    String [] colSeq = sequence.split(":");
                    //Check if size = 2, else ignore, fall at insert
                    if(colSeq.length == 2) {
                        insertableColumnList.add(new InsertableColumn(colSeq[0], dbTable.schema() + "." + colSeq[1] + ".nextval"));
                    }
                }
            }

            for(Map<String,Object> data : dataList)
                this.jdbcSchemaCache.getDialect().processTypes(dbTable, insertableColumns, data);

            log.debug("Finally insertable columns - {}", insertableColumns);

            CreateContext context = new CreateContext(dbTable, insertableColumns, insertableColumnList);
            String sql = sqlCreatorTemplate.create(context);

            log.debug("SQL - {}", sql);
            log.debug("Data - {}", dataList);

            CreateBulkResponse createBulkResponse =
                    this.jdbcSchemaCache.getDialect().supportBatchReturnKeys() ?
                    dbOperationService.batchUpdate(dataList, sql, dbTable) : dbOperationService.batchUpdate(dataList, sql);

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
