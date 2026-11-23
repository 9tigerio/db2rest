package com.homihq.db2rest.jdbc.core.service;

import com.db2rest.jdbc.dialect.Dialect;
import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.core.dto.UpdateResponse;
import com.homihq.db2rest.core.exception.PlaceholderConstraintException;
import com.homihq.db2rest.core.exception.SqlTemplateExecutionException;
import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.template.SqlTemplateLimits;
import com.homihq.db2rest.jdbc.validator.CustomPlaceholderValidators;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.JinjavaConfig;
import com.hubspot.jinjava.tree.parse.DefaultTokenScannerSymbols;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JinJavaTemplateExecutorServiceTest {

    @TempDir
    Path templates;

    private final JdbcManager jdbcManager = mock(JdbcManager.class);
    private final DbOperationService dbOperationService = mock(DbOperationService.class);
    private final NamedParameterJdbcTemplate jdbc = mock(NamedParameterJdbcTemplate.class);
    private final Dialect dialect = mock(Dialect.class);
    private final RecordingTxManager txManager = new RecordingTxManager();
    private final Db2RestConfigProperties properties = new Db2RestConfigProperties();
    private final Jinjava jinjava = new Jinjava(JinjavaConfig.newBuilder()
            .withTokenScannerSymbols(new DefaultTokenScannerSymbols() {
                @Override
                public int getExprStart() {
                    return 0;
                }

                @Override
                public int getExprEnd() {
                    return 0;
                }
            })
            .build());

    private JinJavaTemplateExecutorService service;

    @BeforeEach
    void setUp() {
        properties.setTemplates(templates.toString());
        when(jdbcManager.getDialect("db")).thenReturn(dialect);
        when(jdbcManager.getNamedParameterJdbcTemplate("db")).thenReturn(jdbc);
        when(jdbcManager.getTxnTemplate("db")).thenReturn(new TransactionTemplate(txManager));
        service = new JinJavaTemplateExecutorService(jinjava, properties, dbOperationService, jdbcManager,
                new CustomPlaceholderValidators());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            " ",
            ".hidden",
            "../secret",
            "..\\secret",
            "template..name",
            "/absolute/path",
            "nested/template",
            "nested\\template",
            "template name",
            "template%2Fname"
    })
    void rejectsUnsafeTemplateNames(String templateName) {
        JinJavaTemplateExecutorService unconfigured = new JinJavaTemplateExecutorService(null, null, null, null, null);
        assertThatThrownBy(() -> unconfigured.execute("db", templateName, Map.of()))
                .isInstanceOf(SqlTemplateNotFoundException.class);
    }

    @Test
    void queryRunsWithoutTransaction() throws IOException {
        template("films", "SELECT * FROM film WHERE film_id = {{ params.id | is_required }}");
        when(dbOperationService.read(eq(jdbc), anyMap(), anyString(), eq(dialect))).thenReturn(List.of(Map.of("a", 1)));

        Object result = service.execute("db", "films", Map.of("params", Map.of("id", "7")));

        assertThat(result).isEqualTo(List.of(Map.of("a", 1)));
        verify(dbOperationService).read(jdbc, Map.of("params.id", "7"), "SELECT * FROM film WHERE film_id = :params.id",
                dialect);
        assertThat(txManager.begins).hasValue(0);
    }

    @Test
    void updateCommitsInsideTransaction() throws IOException {
        template("upd", "update film SET release_year = {{ content.year }} WHERE film_id = {{ content.id }}");
        when(dbOperationService.update(eq(jdbc), anyMap(), anyString())).thenAnswer(inv -> {
            assertThat(txManager.active()).isTrue();
            return 1;
        });

        Object result = service.execute("db", "upd", Map.of("content", Map.of("id", 4, "year", 2005)));

        assertThat(result).isEqualTo(new UpdateResponse(1));
        assertThat(txManager.commits).hasValue(1);
        assertThat(txManager.rollbacks).hasValue(0);
    }

    @Test
    void insertAndDeleteAlsoRunInsideTransaction() throws IOException {
        template("ins", "\n\n-- add a category\nINSERT INTO category (name) VALUES ({{ content.name }})");
        template("del", "/* cleanup */\n  DELETE FROM category WHERE category_id = {{ content.id }}");
        CreateResponse created = new CreateResponse(1, Map.of("category_id", 17));
        when(dbOperationService.create(eq(jdbc), anyMap(), anyString(), isNull())).thenAnswer(inv -> {
            assertThat(txManager.active()).isTrue();
            return created;
        });
        when(dbOperationService.delete(eq(jdbc), anyMap(), anyString())).thenAnswer(inv -> {
            assertThat(txManager.active()).isTrue();
            return 1;
        });

        assertThat(service.execute("db", "ins", Map.of("content", Map.of("name", "x")))).isEqualTo(created);
        assertThat(service.execute("db", "del", Map.of("content", Map.of("id", 17)))).isEqualTo(new DeleteResponse(1));
        assertThat(txManager.commits).hasValue(2);
    }

    @Test
    void failedWriteRollsBackAndHidesDatabaseError() throws IOException {
        template("upd", "UPDATE film SET title = {{ content.title }}");
        when(dbOperationService.update(eq(jdbc), anyMap(), anyString()))
                .thenThrow(new DataIntegrityViolationException("null value in column \"title\" violates secret_constraint"));

        assertThatThrownBy(() -> service.execute("db", "upd", Map.of("content", new HashMap<>(Map.of("title", "x")))))
                .isInstanceOf(SqlTemplateExecutionException.class)
                .hasMessage("Template upd failed: the database rejected the statement. The transaction was rolled back.")
                .hasMessageNotContaining("secret_constraint");

        assertThat(txManager.commits).hasValue(0);
        assertThat(txManager.rollbacks).hasValue(1);
    }

    @Test
    void declaredOperationIsUsed() throws IOException {
        template("cte", "-- db2rest:operation=delete\nWITH old AS (SELECT 1) DELETE FROM film WHERE film_id = {{ content.id }}");
        when(dbOperationService.delete(eq(jdbc), anyMap(), anyString())).thenReturn(3);

        assertThat(service.execute("db", "cte", Map.of("content", Map.of("id", 1)))).isEqualTo(new DeleteResponse(3));
        assertThat(txManager.commits).hasValue(1);
    }

    @Test
    void undeterminedStatementTypeNeverReachesDatabase() throws IOException {
        template("merge", "MERGE INTO film USING x ON (1 = 1)");

        assertThatThrownBy(() -> service.execute("db", "merge", Map.of()))
                .isInstanceOf(SqlTemplateValidationException.class);
        verifyNoInteractions(dbOperationService);
    }

    @Test
    void nullRequestValuesAreAllowed() throws IOException {
        template("ins", "INSERT INTO t (a) VALUES ({{ content.a }})");
        Map<String, Object> content = new HashMap<>();
        content.put("a", null);
        when(dbOperationService.create(any(), anyMap(), anyString(), isNull())).thenReturn(new CreateResponse(1, null));

        assertThat(service.execute("db", "ins", Map.of("content", content))).isEqualTo(new CreateResponse(1, null));
    }

    @Test
    void everyFilterOnAPlaceholderIsChecked() throws IOException {
        template("uuid", "SELECT * FROM t WHERE id = {{ params.id | is_required | is_uuid }}");

        assertThatThrownBy(() -> service.execute("db", "uuid", Map.of("params", Map.of("id", "not-a-uuid"))))
                .isInstanceOf(PlaceholderConstraintException.class);
        verifyNoInteractions(dbOperationService);
    }

    @Test
    void renderedSqlIsBounded() throws IOException {
        template("loop", "{% for i in range(1000) %}SELECT 1 UNION ALL {% endfor %}SELECT 1");
        JinJavaTemplateExecutorService small = new JinJavaTemplateExecutorService(jinjava, properties,
                dbOperationService, jdbcManager, new CustomPlaceholderValidators(),
                new SqlTemplateLimits(1000, 500, 10, 4, 10));

        assertThatThrownBy(() -> small.execute("db", "loop", Map.of()))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("rendered SQL is longer than 500 characters");
        verifyNoInteractions(dbOperationService);
    }

    @Test
    void parameterDepthIsBounded() throws IOException {
        template("deep", "SELECT {{ content.a }}");
        Map<String, Object> nested = Map.of("leaf", 1);
        for (int i = 0; i < 10; i++) {
            nested = Map.of("n", nested);
        }
        Map<String, Object> context = Map.of("content", nested);

        assertThatThrownBy(() -> service.execute("db", "deep", context))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("nested deeper than 8 levels");
        verifyNoInteractions(dbOperationService);
    }

    @Test
    void placeholderCountIsBounded() throws IOException {
        template("many", "SELECT " + "{{ params.a }},".repeat(11) + "1");
        JinJavaTemplateExecutorService small = new JinJavaTemplateExecutorService(jinjava, properties,
                dbOperationService, jdbcManager, new CustomPlaceholderValidators(),
                new SqlTemplateLimits(1000, 1000, 10, 4, 10));

        assertThatThrownBy(() -> small.execute("db", "many", Map.of("params", Map.of("a", 1))))
                .isInstanceOf(SqlTemplateValidationException.class)
                .hasMessageContaining("more than 10 placeholders");
    }

    @Test
    void changedTemplateIsPickedUpOnNextRequest() throws IOException {
        Path file = template("films", "SELECT 1");
        when(dbOperationService.read(eq(jdbc), anyMap(), anyString(), eq(dialect))).thenReturn(List.of());
        service.execute("db", "films", Map.of());

        Files.writeString(file, "SELECT 22");
        service.execute("db", "films", Map.of());

        verify(dbOperationService).read(jdbc, Map.of(), "SELECT 22", dialect);
        assertThat(service.templateCacheStats().reloads()).isEqualTo(1);
    }

    @Test
    @Timeout(30)
    void requestsAreNotSerialized() throws Exception {
        template("a", "SELECT 1");
        template("b", "SELECT 2");
        CyclicBarrier inside = new CyclicBarrier(2);
        AtomicBoolean overlapped = new AtomicBoolean();
        when(dbOperationService.read(eq(jdbc), anyMap(), anyString(), eq(dialect))).thenAnswer(inv -> {
            inside.await(10, TimeUnit.SECONDS);
            overlapped.set(true);
            return List.of();
        });

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Future<Object> a = pool.submit(() -> service.execute("db", "a", Map.of()));
            Future<Object> b = pool.submit(() -> service.execute("db", "b", Map.of()));
            a.get(20, TimeUnit.SECONDS);
            b.get(20, TimeUnit.SECONDS);
        } finally {
            pool.shutdownNow();
        }
        assertThat(overlapped).isTrue();
    }

    private Path template(String name, String content) throws IOException {
        return Files.writeString(templates.resolve(name + ".sql"), content);
    }

    /** Transaction manager with real propagation rules that records what happened. */
    static final class RecordingTxManager extends AbstractPlatformTransactionManager {
        private final ThreadLocal<Object[]> current = new ThreadLocal<>();
        final AtomicInteger begins = new AtomicInteger();
        final AtomicInteger commits = new AtomicInteger();
        final AtomicInteger rollbacks = new AtomicInteger();

        boolean active() {
            return current.get() != null;
        }

        @Override
        protected Object doGetTransaction() {
            Object[] tx = current.get();
            return tx != null ? tx : new Object[]{Boolean.FALSE};
        }

        @Override
        protected boolean isExistingTransaction(Object transaction) {
            return Boolean.TRUE.equals(((Object[]) transaction)[0]);
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
            ((Object[]) transaction)[0] = Boolean.TRUE;
            current.set((Object[]) transaction);
            begins.incrementAndGet();
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
            commits.incrementAndGet();
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
            rollbacks.incrementAndGet();
        }

        @Override
        protected void doCleanupAfterCompletion(Object transaction) {
            current.remove();
        }
    }
}
