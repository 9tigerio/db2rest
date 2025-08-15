package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.bulk.FileStreamObserver;
import com.homihq.db2rest.bulk.FileSubject;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.dtos.FileUploadContext;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.jdbc.dto.InsertableColumn;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
public class JdbcBulkCreateService implements BulkCreateService, FileStreamObserver {

    private final TSIDProcessor tsidProcessor;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final JdbcManager jdbcManager;
    private final DbOperationService dbOperationService;
    private final FileSubject fileSubject;
    private FileUploadContext context;

    /**
     * Saves bulk data into the specified table.
     *
     * @param dbId            the database ID
     * @param schemaName      the schema name
     * @param tableName       the table name
     * @param includedColumns the columns to include
     * @param dataList        the data to insert
     * @param tsIdEnabled     whether TSID is enabled
     * @param sequences       the sequences to use
     * @return the response of the bulk create operation
     */
    public CreateBulkResponse saveBulk(
            String dbId,
            String schemaName, String tableName,
            List<String> includedColumns,
            List<Map<String, Object>> dataList,
            boolean tsIdEnabled, List<String> sequences) {

        if (Objects.isNull(dataList) || dataList.isEmpty()) {
            throw new GenericDataAccessException("No data provided");
        }

        log.debug("** Bulk Insert **");

        try {
            DbTable dbTable = jdbcManager.getTable(dbId, schemaName, tableName);
            List<String> insertableColumns = determineInsertableColumns(includedColumns, dataList);
            List<Map<String, Object>> tsIds = handleTsId(tsIdEnabled, dbTable, insertableColumns, dataList);
            List<InsertableColumn> insertableColumnList = convertToInsertableColumnList(insertableColumns, sequences, dbTable);

            processTypes(dbId, dbTable, insertableColumns, dataList);

            CreateContext context = new CreateContext(dbId, dbTable, insertableColumns, insertableColumnList);
            String sql = sqlCreatorTemplate.create(context);

            log.debug("SQL - {}", sql);
            log.debug("Data - {}", dataList);

            CreateBulkResponse createBulkResponse = executeBatchUpdate(dbId, dataList, sql, dbTable);

            if (tsIdEnabled && Objects.isNull(createBulkResponse.keys())) {
                return new CreateBulkResponse(createBulkResponse.rows(), tsIds);
            }

            return createBulkResponse;

        } catch (DataAccessException e) {
            log.error("Error", e);
            throw new GenericDataAccessException(e.getMostSpecificCause().getMessage());
        }
    }

    /**
     * Asynchronously saves a multipart file by streaming its content and performing a bulk insert.
     *
     * @param fileUploadContext the context containing details about the file upload request
     * @param file              the multipart file to be saved
     * @return a CompletableFuture containing the response of the create operation
     */
    @Async
    @Override
    public CompletableFuture<CreateResponse> saveMultipartFile(
            FileUploadContext fileUploadContext,
            MultipartFile file) {

        context = fileUploadContext;

        try (InputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            fileSubject.register(this);

            fileSubject.startStreaming(inputStream);

        } catch (Exception e) {
            log.error("Error during JSON file insert: {}", e.getMessage(), e);
            throw new GenericDataAccessException("Error inserting JSON file: " + e.getMessage());
        }

        return CompletableFuture.completedFuture(new CreateResponse(context.rows(), "Bulk insert completed successfully"));
    }

    /**
     * Updates the observer with the provided data.
     *
     * @param data a list of maps containing the data to update
     */
    @Override
    public void update(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            log.warn("No data to process.");
            return;
        }

        try {
            CreateBulkResponse response = saveBulk(
                    context.dbId(), context.schemaName(), context.tableName(), context.includeColumns(), data,
                    context.tsIdEnabled(), context.sequences());

            context = new FileUploadContext(
                    context.dbId(),
                    context.schemaName(),
                    context.tableName(),
                    context.includeColumns(),
                    context.tsIdEnabled(),
                    context.sequences(),
                    context.rows() + response.rows().length
            );

        } catch (Exception e) {
            fileSubject.unregister();
            log.error("Error during bulk insert: {}", e.getMessage(), e);
            throw new GenericDataAccessException("Error inserting chunk: " + e.getMessage());
        }
    }


    /**
     * Determines the columns that can be inserted.
     *
     * @param includedColumns the columns to include
     * @param dataList        the data to insert
     * @return a list of insertable columns
     */
    private List<String> determineInsertableColumns(
            List<String> includedColumns, List<Map<String, Object>> dataList) {
        return isEmpty(includedColumns)
                ? new ArrayList<>(dataList.get(0).keySet().stream().toList())
                : new ArrayList<>(includedColumns);
    }

    /**
     * Handles TSID processing for the data.
     *
     * @param tsIdEnabled       whether TSID is enabled
     * @param dbTable           the database table
     * @param insertableColumns the columns that can be inserted
     * @param dataList          the data to insert
     * @return a list of TSID maps
     */
    private List<Map<String, Object>> handleTsId(
            boolean tsIdEnabled, DbTable dbTable, List<String> insertableColumns, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> tsIds = new ArrayList<>();
        if (tsIdEnabled) {
            log.debug("Handling TSID");
            List<DbColumn> pkColumns = dbTable.buildPkColumns();

            for (DbColumn pkColumn : pkColumns) {
                log.debug("Adding primary key columns - {}", pkColumn.name());
                insertableColumns.add(pkColumn.name());
            }

            for (Map<String, Object> data : dataList) {
                Map<String, Object> tsIdMap = tsidProcessor.processTsId(data, pkColumns);
                tsIds.add(tsIdMap);
            }
        }
        return tsIds;
    }

    /**
     * Converts the insertable columns to a list of InsertableColumn objects.
     *
     * @param insertableColumns the columns that can be inserted
     * @param sequences         the sequences to use
     * @param dbTable           the database table
     * @return a list of InsertableColumn objects
     */
    private List<InsertableColumn> convertToInsertableColumnList(
            List<String> insertableColumns, List<String> sequences, DbTable dbTable) {
        List<InsertableColumn> insertableColumnList = new ArrayList<>();

        for (String colName : insertableColumns) {
            insertableColumnList.add(new InsertableColumn(colName, null));
        }

        log.debug("Sequences - {}", sequences);
        if (Objects.nonNull(sequences)) {
            for (String sequence : sequences) {
                String[] colSeq = sequence.split(":");
                if (colSeq.length == 2) {
                    insertableColumnList.add(new InsertableColumn(colSeq[0], dbTable.schema() + "." + colSeq[1] + ".nextval"));
                }
            }
        }
        return insertableColumnList;
    }

    /**
     * Processes the types of the data.
     *
     * @param dbId              the database ID
     * @param dbTable           the database table
     * @param insertableColumns the columns that can be inserted
     * @param dataList          the data to insert
     */
    private void processTypes(
            String dbId, DbTable dbTable, List<String> insertableColumns, List<Map<String, Object>> dataList) {
        for (Map<String, Object> data : dataList) {
            this.jdbcManager.getDialect(dbId).processTypes(dbTable, insertableColumns, data);
        }
        log.debug("Finally insertable columns - {}", insertableColumns);
    }

    /**
     * Executes a batch update for the data.
     *
     * @param dbId     the database ID
     * @param dataList the data to insert
     * @param sql      the SQL query
     * @param dbTable  the database table
     * @return the response of the bulk create operation
     */
    private CreateBulkResponse executeBatchUpdate(
            String dbId, List<Map<String, Object>> dataList, String sql, DbTable dbTable) {

        return this.jdbcManager.getTxnTemplate(dbId).execute(status -> {
            try {
                return this.jdbcManager.getDialect(dbId).supportBatchReturnKeys() ?
                        dbOperationService.batchUpdate(
                                jdbcManager.getNamedParameterJdbcTemplate(dbId),
                                dataList, sql, dbTable) :
                        dbOperationService.batchUpdate(jdbcManager.getNamedParameterJdbcTemplate(dbId), dataList, sql);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new GenericDataAccessException("Error Bulk insert - " + e.getMessage());
            }
        });
    }
}
