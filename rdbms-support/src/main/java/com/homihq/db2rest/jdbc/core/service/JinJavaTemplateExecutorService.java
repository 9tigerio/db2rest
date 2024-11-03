package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import com.homihq.db2rest.core.exception.SqlTemplateReadException;
import com.homihq.db2rest.core.exception.UnsupportedConstraintException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.homihq.db2rest.jdbc.dto.Placeholder;
import com.homihq.db2rest.jdbc.validator.ConstraintValidator;
import com.homihq.db2rest.jdbc.validator.CustomPlaceholderValidators;
import com.hubspot.jinjava.Jinjava;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JinJavaTemplateExecutorService implements SQLTemplateExecutorService {

	private final Jinjava jinjava;
	private final Db2RestConfigProperties db2RestConfigProperties;
	private final DbOperationService dbOperationService;
	private final JdbcManager jdbcManager;
	private final CustomPlaceholderValidators validators;
	private final Map<String, String> templateCache = new ConcurrentHashMap<>();
	private static final String SQL_TEMPLATE_EXTENSION = ".sql";
	private static final String PLACEHOLDER_REGEX = "\\{\\{\\s*([^|}]+?)\\s*(?:\\|\\s*([^}]+?))*\\s*}}";

	@Override
	public Object execute(String dbId, String templateFile, Map<String, Object> context) {
		final Pair<String, Map<String, Object>> queryParamPair = executeInternal(templateFile, context);
		final String namedParamsSQL = queryParamPair.getLeft();
		final Map<String, Object> paramMap = queryParamPair.getRight();
		return executeQuery(dbId, paramMap, namedParamsSQL);
	}

	private Object executeQuery(String dbId, Map<String, Object> paramMap, String sql) {
		log.debug("Execute: {}", sql);
		return dbOperationService.read(
				jdbcManager.getNamedParameterJdbcTemplate(dbId),
				paramMap,
				sql,
				jdbcManager.getDialect(dbId)
		);
	}

	private Pair<String, Map<String, Object>> executeInternal(String templateFile, Map<String, Object> context) {
		final String renderedTemplate = renderJinJavaTemplate(templateFile, context);
		final Map<String, Placeholder> placeholders = extractPlaceHolder(renderedTemplate);
		final Map<String, Object> paramMap = buildParamMap(context, "");

		validatePlaceholder(placeholders, paramMap);
		final String namedParamsSQL = buildNamedParameterQuery(renderedTemplate);
		return Pair.of(namedParamsSQL, paramMap);
	}

	private String renderJinJavaTemplate(String templateFile, Map<String, Object> context) {
		log.debug("Rendering query from template {}", templateFile);

		synchronized (templateCache) {
			if (templateCache.containsKey(templateFile)) {
				final String templateContent = templateCache.get(templateFile);
				return jinjava.render(templateContent, context);
			} else {
				final String userTemplateLocation = db2RestConfigProperties.getTemplates();
				final Path templatePath = Paths.get(userTemplateLocation, templateFile + SQL_TEMPLATE_EXTENSION);
				if (!Files.exists(templatePath)) {
					throw new SqlTemplateNotFoundException(templateFile);
				}
				try {
					final String templateContent = Files.readString(templatePath);
					templateCache.put(templateFile, templateContent);
					return jinjava.render(templateContent, context);
				} catch (IOException ioe) {
					log.error("Error reading template file {}: {}", templatePath, ioe.getMessage());
					throw new SqlTemplateReadException(templateFile);
				}
			}
		}
	}

	private Map<String, Placeholder> extractPlaceHolder(final String renderedSqlTemplate) {
		log.debug("Extracting placeholder from sql template");

		Map<String, Placeholder> placeholders = new HashMap<>();
		if (StringUtils.isEmpty(renderedSqlTemplate)) return placeholders;

		Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
		Matcher matcher = pattern.matcher(renderedSqlTemplate);
		while (matcher.find()) {
			String placeholder = matcher.group();
			String namedParam = matcher.group(1).trim();
			List<String> filters = new ArrayList<>();
			for (int i = 2; i <= matcher.groupCount(); i++) {
				String filter = matcher.group(i);
				if (filter != null) {
					filters.add(filter.trim());
				}
			}
			placeholders.put(placeholder, new Placeholder(namedParam, filters));
		}

		return placeholders;
	}

	private void validatePlaceholder(final Map<String, Placeholder> placeholders, Map<String, Object> paramMap) {
		log.debug("Validating placeholders");

		placeholders.forEach((placeholderKey, placeholder) -> {
			List<String> constraints = placeholder.filters();
			Object value = paramMap.get(placeholder.namedParam());
			if (value == null) {
				log.warn("Placeholder {} is missing a value in paramMap", placeholder.namedParam());
			}
			constraints.forEach(constraint -> {
				ConstraintValidator validator = validators.getValidator(constraint);
				if (validator != null) {
					validator.validate(value, placeholder.namedParam());
				} else {
					throw new UnsupportedConstraintException(constraint);
				}
			});
		});
	}

	private static String buildNamedParameterQuery(String template) {
		String replacement = ":$1";
		Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
		Matcher matcher = pattern.matcher(template);
		return matcher.replaceAll(replacement);
	}

	private static Map<String, Object> buildParamMap(Map<String, Object> context, String parentKey) {
		log.debug("Building parameters map");

		return context.entrySet().stream()
				.flatMap(entry -> {
					String key = entry.getKey();
					Object value = entry.getValue();
					String newKey = StringUtils.isEmpty(parentKey) ? key : parentKey + "." + key;

					if (value instanceof Map) {
						return buildParamMap((Map<String, Object>) value, newKey).entrySet().stream();
					} else {
						return java.util.stream.Stream.of(Map.entry(newKey, value));
					}
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
