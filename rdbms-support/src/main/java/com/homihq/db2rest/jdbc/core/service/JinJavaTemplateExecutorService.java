package com.homihq.db2rest.jdbc.core.service;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.exception.SqlTemplateNotFoundException;
import com.homihq.db2rest.core.exception.SqlTemplateReadException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.homihq.db2rest.jdbc.core.DbOperationService;
import com.hubspot.jinjava.Jinjava;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class JinJavaTemplateExecutorService implements SQLTemplateExecutorService {

	private final Jinjava jinjava;
	private final Db2RestConfigProperties db2RestConfigProperties;
	private final DbOperationService dbOperationService;
	private final JdbcManager jdbcManager;
	private final Map<String, String> templateCache = new ConcurrentHashMap<>();

	@Override
	public Object execute(String dbId, String templateFile, Map<String, Object> context)  {
		String sql = executeInternal(dbId, templateFile, context);
		return execute(dbId, sql);
	}


	public String executeInternal(String dbId, String templateFile, Map<String, Object> context)  {
		log.debug("Rendering query from template {}", templateFile);

		if(templateCache.containsKey(templateFile)) {
			final String templateContent = templateCache.get(templateFile);
			return jinjava.render(templateContent, context);
		}
		else {

			final String userTemplateLocation = db2RestConfigProperties.getTemplates();
			final Path templatePath = Paths.get(userTemplateLocation, templateFile + ".sql");

			if (!Files.exists(templatePath)) {
				throw new SqlTemplateNotFoundException(templateFile);
			}
			try {
				final String templateContent = Files.readString(templatePath);

				templateCache.put(templateFile, templateContent);

				return jinjava.render(templateContent, context);
			} catch (IOException ioe) {
				throw new SqlTemplateReadException(templateFile);
			}
		}
	}


	public Object execute(String sql, String dbId) {
		log.debug("Execute: {}", sql);
		return dbOperationService.query(
				jdbcManager.getNamedParameterJdbcTemplate(dbId),
				sql,
				jdbcManager.getDialect(dbId)
		);
	}
}
