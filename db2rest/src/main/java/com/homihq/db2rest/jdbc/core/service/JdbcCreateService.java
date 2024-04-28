package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.core.model.DbColumn;
import com.homihq.db2rest.jdbc.core.model.DbTable;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.dto.InsertableColumn;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
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
public class JdbcCreateService implements CreateService {

    private final TSIDProcessor tsidProcessor;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final JdbcSchemaCache jdbcSchemaCache;
    private final DbOperationService dbOperationService;


    @Override
    @Transactional
    public CreateResponse save(String schemaName, String tableName, List<String> includedColumns,
                               Map<String, Object> data, boolean tsIdEnabled, List<String> sequences) {
        try {
            //1. get actual table
            DbTable dbTable = jdbcSchemaCache.getTable(schemaName, tableName);

            //2. determine the columns to be included in insert statement
            List<String> insertableColumns = isEmpty(includedColumns) ? new ArrayList<>(data.keySet().stream().toList()) :
                    new ArrayList<>(includedColumns);

            //3. check if tsId is enabled and add those values for PK.
            Map<String,Object> tsIdMap = null;
            if (tsIdEnabled) {
                List<DbColumn> pkColumns = dbTable.buildPkColumns();

                for(DbColumn pkColumn : pkColumns) {
                    log.debug("Adding primary key columns - {}", pkColumn.name());
                    insertableColumns.add(pkColumn.name());
                }

                tsIdMap = tsidProcessor.processTsId(data, pkColumns);
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

            this.jdbcSchemaCache.getDialect().processTypes(dbTable, insertableColumns, data);

            CreateContext context = new CreateContext(dbTable, insertableColumns, insertableColumnList);
            String sql = sqlCreatorTemplate.create(context);


            log.info("SQL - {}", sql);
            log.info("Data - {}", data);


            CreateResponse createResponse = dbOperationService.create(data, sql, dbTable);

            if(tsIdEnabled && Objects.isNull(createResponse.keys())) {
                return new CreateResponse(createResponse.row(), tsIdMap);
            }

            return createResponse;
        } catch (DataAccessException e) {

            log.error("Error", e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }


    }

}
