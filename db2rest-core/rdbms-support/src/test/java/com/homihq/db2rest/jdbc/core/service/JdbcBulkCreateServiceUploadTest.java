package com.homihq.db2rest.jdbc.core.service;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.bulk.JsonFileDataProcessor;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.exception.BulkUploadException;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.dtos.BulkContext;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.CreateContext;
import com.homihq.db2rest.jdbc.sql.SqlCreatorTemplate;
import com.homihq.db2rest.jdbc.tsid.TSIDProcessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionTimedOutException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Upload behaviour of {@link JdbcBulkCreateService} against a fake database whose
 * transaction manager has real Spring propagation semantics: rows written by a
 * batch only become visible once the outer transaction commits.
 */
class JdbcBulkCreateServiceUploadTest {

    private static final int CHUNK = 500;

    private final JdbcManager jdbcManager = mock(JdbcManager.class);
    private final DbOperationService dbOperationService = mock(DbOperationService.class);
    private final SqlCreatorTemplate sqlCreatorTemplate = mock(SqlCreatorTemplate.class);
    private final Dialect dialect = mock(Dialect.class);
    private final DbTable actorTable = mock(DbTable.class);
    private final DbTable directorTable = mock(DbTable.class);
    private final NamedParameterJdbcTemplate templateA = mock(NamedParameterJdbcTemplate.class);
    private final NamedParameterJdbcTemplate templateB = mock(NamedParameterJdbcTemplate.class);
    private final FakeDb dbA = new FakeDb("dbA");
    private final FakeDb dbB = new FakeDb("dbB");

    /** Runs inside every batch insert, after the rows are staged. */
    private volatile Consumer<Write> onBatch = w -> { };

    private JdbcBulkCreateService service;

    @BeforeEach
    void setUp() {
        when(jdbcManager.getDialect(anyString())).thenReturn(dialect);
        when(dialect.supportBatchReturnKeys()).thenReturn(false);
        when(jdbcManager.getTable("dbA", null, "actor")).thenReturn(actorTable);
        when(jdbcManager.getTable("dbB", "s2", "director")).thenReturn(directorTable);
        when(jdbcManager.getNamedParameterJdbcTemplate("dbA")).thenReturn(templateA);
        when(jdbcManager.getNamedParameterJdbcTemplate("dbB")).thenReturn(templateB);
        when(jdbcManager.getTxnTemplate("dbA")).thenReturn(new TransactionTemplate(dbA));
        when(jdbcManager.getTxnTemplate("dbB")).thenReturn(new TransactionTemplate(dbB));
        when(sqlCreatorTemplate.create(any(CreateContext.class))).thenAnswer(inv -> {
            CreateContext ctx = inv.getArgument(0);
            return "insert into " + ctx.dbId() + "." + (ctx.table() == actorTable ? "actor" : "director");
        });
        when(dbOperationService.batchUpdate(any(NamedParameterJdbcTemplate.class), anyList(), anyString()))
                .thenAnswer(inv -> {
                    NamedParameterJdbcTemplate template = inv.getArgument(0);
                    List<Map<String, Object>> rows = inv.getArgument(1);
                    FakeDb db = template == templateA ? dbA : dbB;
                    Write write = new Write(db.name, inv.getArgument(2), List.copyOf(rows));
                    db.stage(write);
                    onBatch.accept(write);
                    return new CreateBulkResponse(new int[rows.size()], null);
                });

        service = new JdbcBulkCreateService(mock(TSIDProcessor.class), sqlCreatorTemplate, jdbcManager,
                dbOperationService, new JsonFileDataProcessor(), 42, CHUNK);
    }

    @AfterEach
    void clearInterrupt() {
        Thread.interrupted();
    }

    @RepeatedTest(25)
    @Timeout(60)
    void overlappingUploadsKeepTheirOwnDestinationAndCount() throws Exception {
        CyclicBarrier bothInFlight = new CyclicBarrier(2);
        AtomicInteger firstBatches = new AtomicInteger();
        onBatch = w -> {
            // the first chunk of each upload waits until the other upload has written one too
            if (w.rows().get(0).get("id").equals(0)) {
                firstBatches.incrementAndGet();
                awaitQuietly(bothInFlight);
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<CreateResponse> a = pool.submit(() ->
                    service.saveMultipartFile(context("dbA", null, "actor"), file(rows("A", 1200))).join());
            Future<CreateResponse> b = pool.submit(() ->
                    service.saveMultipartFile(context("dbB", "s2", "director"), file(rows("B", 700))).join());

            assertThat(a.get(30, TimeUnit.SECONDS).row()).isEqualTo(1200);
            assertThat(b.get(30, TimeUnit.SECONDS).row()).isEqualTo(700);
        } finally {
            pool.shutdownNow();
        }

        assertThat(firstBatches).hasValue(2);
        assertCommitted(dbA, "insert into dbA.actor", "A", 1200);
        assertCommitted(dbB, "insert into dbB.director", "B", 700);
        assertThat(dbA.commits).hasValue(1);
        assertThat(dbB.commits).hasValue(1);
        assertThat(dbA.rollbacks).hasValue(0);
        assertThat(dbB.rollbacks).hasValue(0);
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 499, 500, 501, 1100})
    void malformedJsonRollsBackEveryChunk(int goodRows) {
        String content = rows("A", goodRows).replaceAll("]$", ",{\"id\": broken}]");

        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), file(content)))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is not valid JSON")
                .hasMessageEndingWith(BulkUploadException.NOTHING_COMMITTED);

        assertThat(dbA.staged).hasSize(goodRows / CHUNK);
        assertThat(dbA.committed).isEmpty();
        assertThat(dbA.commits).hasValue(0);
        assertThat(dbA.rollbacks).hasValue(1);
        assertThat(dbA.open()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "{\"id\":1}", "[]", "[1,2]", "[{\"id\":1}] trailing"})
    void invalidFileIsRejectedWithoutCommit(String content) {
        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), file(content)))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageEndingWith(BulkUploadException.NOTHING_COMMITTED);

        assertThat(dbA.committed).isEmpty();
        assertThat(dbA.commits).hasValue(0);
    }

    @Test
    void zeroByteFileIsRejectedBeforeTransactionStarts() {
        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), file("")))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload file is empty.");
        assertThat(dbA.begins).hasValue(0);
    }

    @Test
    void databaseErrorIsSanitizedAndRolledBack() {
        AtomicInteger batches = new AtomicInteger();
        onBatch = w -> {
            if (batches.incrementAndGet() == 2) {
                throw new DuplicateKeyException("duplicate key value violates unique constraint \"secret_pk\"");
            }
        };
        TrackingFile upload = file(rows("A", 1200));

        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), upload))
                .isInstanceOf(BulkUploadException.class)
                .hasMessage("The database rejected the rows at array index 500 to 999. "
                        + BulkUploadException.NOTHING_COMMITTED)
                .hasMessageNotContaining("secret_pk");

        assertThat(dbA.committed).isEmpty();
        assertThat(dbA.rollbacks).hasValue(1);
        assertThat(dbA.open()).isFalse();
        assertThat(upload.streamsClosed()).isTrue();
    }

    @Test
    void unknownTableKeepsItsOwnErrorAndRollsBack() {
        when(jdbcManager.getTable("dbA", null, "missing")).thenThrow(new InvalidTableException("missing"));

        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "missing"), file(rows("A", 3))))
                .isInstanceOf(InvalidTableException.class);
        assertThat(dbA.commits).hasValue(0);
        assertThat(dbA.open()).isFalse();
    }

    @Test
    void interruptedUploadStopsAtNextChunkAndRollsBack() {
        onBatch = w -> Thread.currentThread().interrupt();
        TrackingFile upload = file(rows("A", 1200));

        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), upload))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload was cancelled.");

        assertThat(dbA.staged).hasSize(1);
        assertThat(dbA.committed).isEmpty();
        assertThat(dbA.rollbacks).hasValue(1);
        assertThat(dbA.open()).isFalse();
        assertThat(upload.streamsClosed()).isTrue();
    }

    @Test
    void timeoutIsAppliedAndReported() {
        onBatch = w -> {
            throw new TransactionTimedOutException("Transaction timed out: deadline was ...");
        };

        assertThatThrownBy(() -> service.saveMultipartFile(context("dbA", null, "actor"), file(rows("A", 10))))
                .isInstanceOf(BulkUploadException.class)
                .hasMessageStartingWith("Upload did not finish within 42 seconds.");

        assertThat(dbA.lastTimeout).isEqualTo(42);
        assertThat(dbA.committed).isEmpty();
        assertThat(dbA.rollbacks).hasValue(1);
    }

    @Test
    void successfulUploadClosesStreamAndCountsCommittedRows() {
        TrackingFile upload = file(rows("A", 1001));

        CreateResponse response = service.saveMultipartFile(context("dbA", null, "actor"), upload).join();

        assertThat(response.row()).isEqualTo(1001);
        assertThat(dbA.committed).hasSize(1001);
        assertThat(dbA.staged).hasSize(3);
        assertThat(upload.streamsClosed()).isTrue();
        assertThat(dbA.open()).isFalse();
    }

    private static void assertCommitted(FakeDb db, String sql, String src, int count) {
        assertThat(db.writes).allSatisfy(w -> assertThat(w.sql()).isEqualTo(sql));
        assertThat(db.committed).hasSize(count);
        assertThat(db.committed).allSatisfy(r -> assertThat(r.get("src")).isEqualTo(src));
        assertThat(db.committed.stream().map(r -> r.get("id")).distinct().count()).isEqualTo(count);
    }

    private static BulkContext context(String dbId, String schema, String table) {
        return new BulkContext(dbId, schema, table, null, false, null, 0, null);
    }

    private static String rows(String src, int count) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("{\"id\":").append(i).append(",\"src\":\"").append(src).append("\"}");
        }
        return sb.append(']').toString();
    }

    private static void awaitQuietly(CyclicBarrier barrier) {
        try {
            barrier.await(20, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new IllegalStateException("uploads did not overlap", e);
        }
    }

    private static TrackingFile file(String content) {
        return new TrackingFile(content.getBytes(StandardCharsets.UTF_8));
    }

    record Write(String db, String sql, List<Map<String, Object>> rows) {
    }

    /** Minimal transactional store: staged writes are applied on commit, dropped on rollback. */
    static final class FakeDb extends AbstractPlatformTransactionManager {
        final String name;
        final ThreadLocal<Tx> current = new ThreadLocal<>();
        final List<Write> writes = new CopyOnWriteArrayList<>();
        final List<Write> staged = new CopyOnWriteArrayList<>();
        final List<Map<String, Object>> committed = new CopyOnWriteArrayList<>();
        final AtomicInteger begins = new AtomicInteger();
        final AtomicInteger commits = new AtomicInteger();
        final AtomicInteger rollbacks = new AtomicInteger();
        volatile int lastTimeout;

        FakeDb(String name) {
            this.name = name;
        }

        static final class Tx {
            boolean active;
            final List<Write> pending = new ArrayList<>();
        }

        void stage(Write write) {
            Tx tx = current.get();
            if (tx == null || !tx.active) {
                throw new IllegalStateException("batch executed outside a transaction");
            }
            tx.pending.add(write);
            writes.add(write);
            staged.add(write);
        }

        boolean open() {
            return current.get() != null;
        }

        @Override
        protected Object doGetTransaction() {
            Tx tx = current.get();
            return tx != null ? tx : new Tx();
        }

        @Override
        protected boolean isExistingTransaction(Object transaction) {
            return ((Tx) transaction).active;
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            Tx tx = (Tx) transaction;
            tx.active = true;
            current.set(tx);
            begins.incrementAndGet();
            lastTimeout = definition.getTimeout();
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            ((Tx) status.getTransaction()).pending.forEach(w -> committed.addAll(w.rows()));
            commits.incrementAndGet();
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            ((Tx) status.getTransaction()).pending.clear();
            rollbacks.incrementAndGet();
        }

        @Override
        protected void doSetRollbackOnly(DefaultTransactionStatus status) {
            // the outer transaction rolls back because the exception reaches it
        }

        @Override
        protected void doCleanupAfterCompletion(Object transaction) {
            current.remove();
        }
    }

    /** MultipartFile over a byte array that records whether every opened stream was closed. */
    static final class TrackingFile implements MultipartFile {
        private final byte[] content;
        private final List<Boolean> closed = new CopyOnWriteArrayList<>();

        TrackingFile(byte[] content) {
            this.content = content;
        }

        boolean streamsClosed() {
            return !closed.isEmpty() && !closed.contains(Boolean.FALSE);
        }

        @Override
        public InputStream getInputStream() {
            int slot = closed.size();
            closed.add(Boolean.FALSE);
            return new ByteArrayInputStream(content) {
                @Override
                public void close() {
                    closed.set(slot, Boolean.TRUE);
                }
            };
        }

        @Override
        public String getName() {
            return "file";
        }

        @Override
        public String getOriginalFilename() {
            return "upload.json";
        }

        @Override
        public String getContentType() {
            return "application/json";
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content.clone();
        }

        @Override
        public void transferTo(File dest) {
            throw new UnsupportedOperationException();
        }
    }
}
