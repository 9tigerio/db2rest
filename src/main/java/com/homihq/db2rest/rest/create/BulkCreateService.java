package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.dbop.JdbcOperationService;
import com.homihq.db2rest.exception.GenericDataAccessException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import com.homihq.db2rest.rest.create.dto.CreateContext;
import com.homihq.db2rest.rest.create.tsid.TSIDProcessor;
import com.homihq.db2rest.schema.JdbcSchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkCreateService {

    private final TSIDProcessor tsidProcessor;
    private final CreateCreatorTemplate createCreatorTemplate;
    private final JdbcSchemaManager jdbcSchemaManager;
    private final JdbcOperationService jdbcOperationService;

    @Transactional
    public CreateBulkResponse saveBulk(String schemaName, String tableName,
                                                           List<String> includedColumns,
                                                           List<Map<String, Object>> dataList,
                                                           boolean tsIdEnabled) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) throw new GenericDataAccessException("No data provided");

        log.info("** Bulk Insert **");

        try {

            //1. get actual table
            DbTable dbTable = StringUtils.isNotBlank(schemaName) ?
                    jdbcSchemaManager.getOneTable(schemaName, tableName) : jdbcSchemaManager.getTable(tableName);

            //2. determine the columns to be included in insert statement
            List<String> insertableColumns = isEmpty(includedColumns) ? new ArrayList<>(dataList.get(0).keySet().stream().toList()) :
                    new ArrayList<>(includedColumns);

            //3. check if tsId is enabled and add those values for PK.
            if (tsIdEnabled) {
                log.debug("Handling TSID");
                List<DbColumn> pkColumns = dbTable.buildPkColumns();

                for(DbColumn pkColumn : pkColumns) {
                    log.debug("Adding primary key columns - {}", pkColumn.name());
                    insertableColumns.add(pkColumn.name());
                }

                for (Map<String, Object> data : dataList) {
                    tsidProcessor.processTsId(data, pkColumns);
                }
            }

            for(Map<String,Object> data : dataList)
                this.jdbcSchemaManager.getDialect().processTypes(dbTable, insertableColumns, data);

            log.debug("Finally insertable columns - {}", insertableColumns);

            CreateContext context = new CreateContext(dbTable, insertableColumns);
            String sql = createCreatorTemplate.createQuery(context);

            log.info("SQL - {}", sql);
            log.info("Data - {}", dataList);

            return jdbcOperationService.batchUpdate(dataList, sql, dbTable);
        } catch (DataAccessException e) {
            log.error("Error", e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }


    }



}
