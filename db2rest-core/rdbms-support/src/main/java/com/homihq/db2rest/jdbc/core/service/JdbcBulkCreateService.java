package com.homihq.db2rest.jdbc.core.service;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.bulk.JsonChunkHandler;
import com.homihq.db2rest.bulk.JsonFileDataProcessor;
import com.homihq.db2rest.config.MultiTenancy;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.exception.BulkUploadException;
import com.homihq.db2rest.core.exception.BulkWriteException;
import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.core.exception.InvalidColumnException;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.dtos.BulkContext;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.jdbc.dto.InsertableColumn;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Bulk insert service. The bean is a singleton shared by all requests, so it holds
 * no request state: everything an insert or an upload needs is passed in or kept
 * on the stack of the call.
 */
@Slf4j
public class JdbcBulkCreateService implements BulkCreateService {

    public static final int DEFAULT_UPLOAD_TIMEOUT_SECONDS = 300;

    private final TSIDProcessor tsidProcessor;
    private final SqlCreatorTemplate sqlCreatorTemplate;
    private final JdbcManager jdbcManager;
    private final DbOperationService dbOperationService;
    private final JsonFileDataProcessor jsonFileDataProcessor;
    private final int uploadTimeoutSeconds;
    private final int chunkSize;

    public JdbcBulkCreateService(TSIDProcessor tsidProcessor,
                                 SqlCreatorTemplate sqlCreatorTemplate,
                                 JdbcManager jdbcManager,
                                 DbOperationService dbOperationService,
                                 JsonFileDataProcessor jsonFileDataProcessor) {
        this(tsidProcessor, sqlCreatorTemplate, jdbcManager, dbOperationService, jsonFileDataProcessor,
                DEFAULT_UPLOAD_TIMEOUT_SECONDS, JsonFileDataProcessor.DEFAULT_CHUNK_SIZE);
    }

    public JdbcBulkCreateService(TSIDProcessor tsidProcessor,
                                 SqlCreatorTemplate sqlCreatorTemplate,
                                 JdbcManager jdbcManager,
                                 DbOperationService dbOperationService,
                                 JsonFileDataProcessor jsonFileDataProcessor,
                                 int uploadTimeoutSeconds,
                                 int chunkSize) {
        if (uploadTimeoutSeconds < 1 || chunkSize < 1) {
            throw new IllegalArgumentException("uploadTimeoutSeconds and chunkSize must be positive");
        }
        this.tsidProcessor = tsidProcessor;
        this.sqlCreatorTemplate = sqlCreatorTemplate;
        this.jdbcManager = jdbcManager;
        this.dbOperationService = dbOperationService;
        this.jsonFileDataProcessor = jsonFileDataProcessor;
        this.uploadTimeoutSeconds = uploadTimeoutSeconds;
        this.chunkSize = chunkSize;
    }

    /**
     * Saves bulk data into the specified table.
     * @return the response of the bulk create operation
     */
    public CreateBulkResponse saveBulk(BulkContext bulkContext, List<Map<String, Object>> dataList) {
        if (Objects.isNull(dataList) || dataList.isEmpty()) {
            throw new GenericDataAccessException("No data provided");
        }
        MultiTenancy.addTenantColumns(dataList, bulkContext.dbId(), bulkContext.tableName(), bulkContext.roleFilterContext());

        log.debug("** Bulk Insert **");

        String dbId = bulkContext.dbId();
        try {
            DbTable dbTable = jdbcManager.getTable(dbId, bulkContext.schemaName(), bulkContext.tableName());
            List<String> insertableColumns = determineInsertableColumns(bulkContext.includeColumns(), dataList);
            List<Map<String, Object>> tsIds = handleTsId(bulkContext.tsIdEnabled(), dbTable, insertableColumns, dataList);
            List<InsertableColumn> insertableColumnList = convertToInsertableColumnList(insertableColumns, bulkContext.sequences(), dbTable);

            processTypes(dbId, dbTable, insertableColumns, dataList);

            CreateContext context = new CreateContext(dbId, dbTable, insertableColumns, insertableColumnList);
            String sql = sqlCreatorTemplate.create(context);

            log.debug("SQL - {}", sql);
            log.debug("Data - {}", dataList);

            CreateBulkResponse createBulkResponse = executeBatchUpdate(dbId, dataList, sql, dbTable);

            if (bulkContext.tsIdEnabled() && Objects.isNull(createBulkResponse.keys())) {
                return new CreateBulkResponse(createBulkResponse.rows(), tsIds);
            }

            return createBulkResponse;

        } catch (DataAccessException e) {
            log.error("Error", e);
            throw new BulkWriteException(e.getMostSpecificCause().getMessage(), e);
        }
    }

    /**
     * Imports a JSON array file in chunks.
     * <p>
     * The whole file is imported in one transaction: either every row is committed and the
     * response count equals the number of rows written, or nothing is committed and a
     * {@link BulkUploadException} is thrown. The transaction is bounded by the upload
     * timeout. The upload runs on the calling thread and the returned future is already
     * complete; an interrupted thread stops the upload at the next chunk.
     * <p>
     * The destination, parser and row counter are local to this call, so concurrent
     * uploads cannot see or change each other's state.
     */
    @Override
    public CompletableFuture<CreateResponse> saveMultipartFile(BulkContext bulkContext, MultipartFile file) {
        Objects.requireNonNull(bulkContext, "bulkContext");
        if (file == null || file.isEmpty()) {
            throw BulkUploadException.emptyFile();
        }

        String dbId = bulkContext.dbId();
        jdbcManager.getDialect(dbId); // unknown dbId fails here, before any work starts
        TransactionTemplate uploadTxn = uploadTransaction(dbId);

        Long rows;
        try {
            rows = uploadTxn.execute(status -> importFile(bulkContext, file));
        } catch (TransactionTimedOutException e) {
            throw BulkUploadException.timedOut(uploadTimeoutSeconds, e);
        } catch (TransactionException e) {
            log.error("Bulk upload to {}.{} could not be committed", dbId, bulkContext.tableName(), e);
            throw BulkUploadException.commitFailed(e);
        }

        int committed = Math.toIntExact(Objects.requireNonNull(rows));
        return CompletableFuture.completedFuture(new CreateResponse(committed, "Bulk insert completed successfully"));
    }

    private TransactionTemplate uploadTransaction(String dbId) {
        TransactionTemplate base = jdbcManager.getTxnTemplate(dbId);
        TransactionTemplate uploadTxn = new TransactionTemplate(base.getTransactionManager(), base);
        uploadTxn.setTimeout(uploadTimeoutSeconds);
        return uploadTxn;
    }

    private long importFile(BulkContext bulkContext, MultipartFile file) {
        long[] inserted = {0};
        JsonChunkHandler handler = (rows, firstIndex) -> {
            if (Thread.currentThread().isInterrupted()) {
                throw BulkUploadException.cancelled();
            }
            inserted[0] += insertChunk(bulkContext, rows, firstIndex);
        };

        try (InputStream inputStream = file.getInputStream()) {
            jsonFileDataProcessor.stream(inputStream, chunkSize, handler);
        } catch (IOException e) {
            throw BulkUploadException.unreadable(e);
        }
        return inserted[0];
    }

    private int insertChunk(BulkContext bulkContext, List<Map<String, Object>> rows, long firstIndex) {
        long lastIndex = firstIndex + rows.size() - 1;
        try {
            return saveBulk(bulkContext, rows).rows().length;
        } catch (InvalidTableException | InvalidColumnException | BulkUploadException e) {
            throw e;
        } catch (RuntimeException e) {
            if (hasCause(e, TransactionTimedOutException.class)) {
                throw BulkUploadException.timedOut(uploadTimeoutSeconds, e);
            }
            if (e instanceof GenericDataAccessException && !(e instanceof BulkWriteException)) {
                // raised by request validation before anything reached the database
                throw new BulkUploadException(e.getMessage(), e);
            }
            log.error("Bulk upload to {}.{} failed for rows {} to {}",
                    bulkContext.dbId(), bulkContext.tableName(), firstIndex, lastIndex, e);
            throw BulkUploadException.rejected(firstIndex, lastIndex, e);
        }
    }

    private static boolean hasCause(Throwable e, Class<? extends Throwable> type) {
        for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
            if (type.isInstance(t)) {
                return true;
            }
        }
        return false;
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
     * Executes a batch update for the data. When called inside an upload, the batch joins
     * the upload's transaction instead of committing on its own.
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
                throw new BulkWriteException("Error Bulk insert - " + e.getMessage(), e);
            }
        });
    }
}
