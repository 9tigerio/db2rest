package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.core.dto.UpdateResponse;
import com.homihq.db2rest.core.exception.SqlTemplateExecutionException;
import com.homihq.db2rest.core.exception.SqlTemplateValidationException;
import com.homihq.db2rest.core.exception.UnsupportedConstraintException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.Placeholder;
import com.homihq.db2rest.jdbc.template.PlaceholderScanner;
import com.homihq.db2rest.jdbc.template.SqlTemplateLimits;
import com.homihq.db2rest.jdbc.template.SqlTemplateOperation;
import com.homihq.db2rest.jdbc.template.SqlTemplateStore;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;
import com.homihq.db2rest.jdbc.validator.CustomPlaceholderValidators;
import com.hubspot.jinjava.Jinjava;
import com.hubspot.jinjava.interpret.FatalTemplateErrorsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.TransactionException;

import java.util.HashMap;
import java.util.Map;

/**
 * Executes SQL templates stored as files in the configured template directory.
 * <p>
 * A request goes through these steps:
 * <ol>
 *   <li>the template is loaded through {@link SqlTemplateStore}, which validates the
 *       name, keeps the file inside the template directory and caches its contents;</li>
 *   <li>Jinjava renders the source (only tags such as {@code {% if %}}; expression output
 *       is disabled, so request values never become SQL text);</li>
 *   <li>{@link PlaceholderScanner} turns {@code {{ name | filter }}} placeholders into
 *       named parameters and the filters are checked against the request values;</li>
 *   <li>the statement type is taken from the template declaration or the first keyword
 *       ({@link SqlTemplateOperation}); insert, update and delete statements run in the
 *       database's transaction template, so they commit on success and roll back on
 *       failure, like the CRUD endpoints.</li>
 * </ol>
 * All sizes and counts are bounded by {@link SqlTemplateLimits}. The service holds no
 * lock while rendering or executing.
 */
@Slf4j
public class JinJavaTemplateExecutorService implements SQLTemplateExecutorService {

    private final Jinjava jinjava;
    private final DbOperationService dbOperationService;
    private final JdbcManager jdbcManager;
    private final CustomPlaceholderValidators validators;
    private final SqlTemplateLimits limits;
    private final SqlTemplateStore store;

    public JinJavaTemplateExecutorService(Jinjava jinjava,
                                          Db2RestConfigProperties db2RestConfigProperties,
                                          DbOperationService dbOperationService,
                                          JdbcManager jdbcManager,
                                          CustomPlaceholderValidators validators) {
        this(jinjava, db2RestConfigProperties, dbOperationService, jdbcManager, validators,
                SqlTemplateLimits.DEFAULTS);
    }

    public JinJavaTemplateExecutorService(Jinjava jinjava,
                                          Db2RestConfigProperties db2RestConfigProperties,
                                          DbOperationService dbOperationService,
                                          JdbcManager jdbcManager,
                                          CustomPlaceholderValidators validators,
                                          SqlTemplateLimits limits) {
        this.jinjava = jinjava;
        this.dbOperationService = dbOperationService;
        this.jdbcManager = jdbcManager;
        this.validators = validators;
        this.limits = limits;
        this.store = new SqlTemplateStore(() -> db2RestConfigProperties.getTemplates(), limits);
    }

    @Override
    public Object execute(String dbId, String templateName, Map<String, Object> context) {
        SqlTemplateStore.Template template = store.load(templateName);

        String rendered = render(template, context);
        PlaceholderScanner.Result scanned =
                PlaceholderScanner.scan(templateName, rendered, limits.maxPlaceholders());
        Map<String, Object> paramMap = buildParamMap(templateName, context);
        validatePlaceholders(scanned, paramMap);

        SqlTemplateOperation operation =
                SqlTemplateOperation.resolve(templateName, template.declaredOperation(), scanned.sql());
        log.debug("Template {} resolved to {}: {}", templateName, operation, scanned.sql());

        return run(dbId, templateName, operation, paramMap, scanned.sql());
    }

    /** Cache counters, for metrics. */
    public SqlTemplateStore.CacheStats templateCacheStats() {
        return store.stats();
    }

    /** Clears the template cache; templates are read again on next use. */
    public void invalidateTemplateCache() {
        store.invalidateAll();
    }

    private String render(SqlTemplateStore.Template template, Map<String, Object> context) {
        String rendered;
        try {
            rendered = jinjava.render(template.source(), context);
        } catch (FatalTemplateErrorsException e) {
            log.error("Template {} could not be rendered: {}", template.name(), e.getMessage());
            throw new SqlTemplateValidationException(template.name(), "template could not be rendered");
        }
        if (rendered.length() > limits.maxRenderedChars()) {
            throw new SqlTemplateValidationException(template.name(),
                    "rendered SQL is longer than " + limits.maxRenderedChars() + " characters");
        }
        return rendered;
    }

    private Object run(String dbId, String templateName, SqlTemplateOperation operation,
                       Map<String, Object> paramMap, String sql) {
        var dialect = jdbcManager.getDialect(dbId);
        NamedParameterJdbcTemplate jdbc = jdbcManager.getNamedParameterJdbcTemplate(dbId);

        if (!operation.isWrite()) {
            try {
                return dbOperationService.read(jdbc, paramMap, sql, dialect);
            } catch (DataAccessException e) {
                log.error("Template {} query failed", templateName, e);
                throw new SqlTemplateExecutionException(templateName, false);
            }
        }

        try {
            return jdbcManager.getTxnTemplate(dbId).execute(status -> {
                try {
                    return switch (operation) {
                        case INSERT -> dbOperationService.create(jdbc, paramMap, sql, null);
                        case UPDATE -> new UpdateResponse(dbOperationService.update(jdbc, paramMap, sql));
                        case DELETE -> new DeleteResponse(dbOperationService.delete(jdbc, paramMap, sql));
                        case QUERY -> throw new IllegalStateException("not a write");
                    };
                } catch (DataAccessException e) {
                    status.setRollbackOnly();
                    log.error("Template {} {} failed, rolling back", templateName, operation, e);
                    throw new SqlTemplateExecutionException(templateName, true);
                }
            });
        } catch (TransactionException e) {
            log.error("Template {} {} could not be committed", templateName, operation, e);
            throw new SqlTemplateExecutionException(templateName, true);
        }
    }

    private Map<String, Object> buildParamMap(String templateName, Map<String, Object> context) {
        Map<String, Object> params = new HashMap<>();
        flatten(templateName, context, "", 1, params);
        return params;
    }

    private void flatten(String templateName, Map<?, ?> values, String prefix, int depth,
                         Map<String, Object> into) {
        if (depth > limits.maxParamDepth()) {
            throw new SqlTemplateValidationException(templateName,
                    "request values are nested deeper than " + limits.maxParamDepth() + " levels");
        }
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map<?, ?> nested) {
                flatten(templateName, nested, key, depth + 1, into);
            } else {
                into.put(key, entry.getValue());
            }
        }
    }

    private void validatePlaceholders(PlaceholderScanner.Result scanned, Map<String, Object> paramMap) {
        for (Placeholder placeholder : scanned.placeholders()) {
            Object value = paramMap.get(placeholder.namedParam());
            if (value == null) {
                log.warn("Placeholder {} is missing a value in paramMap", placeholder.namedParam());
            }
            for (String constraint : placeholder.filters()) {
                ConstraintValidator validator = validators.getValidator(constraint);
                if (validator == null) {
                    throw new UnsupportedConstraintException(constraint);
                }
                validator.validate(value, placeholder.namedParam());
            }
        }
    }
}
